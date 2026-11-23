package ru.spbpu.tails.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.application.dto.request.CreateApplicationRequest;
import ru.spbpu.tails.application.dto.request.UpdateApplicationStatusRequest;
import ru.spbpu.tails.application.dto.response.ApplicationResponse;
import ru.spbpu.tails.application.dto.response.CreateApplicationResponse;
import ru.spbpu.tails.application.dto.response.UpdateApplicationStatusResponse;
import ru.spbpu.tails.application.service.ApplicationService;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.Species;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ApplicationRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    private static final Long SHELTER_ID = 42L;
    private static final Long ANIMAL_ID = 7L;
    private static final Long APPLICATION_ID = 19L;
    private static final String SHELTER_EMAIL = "shelter@example.com";

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ShelterRepository shelterRepository;

    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationService(
                animalRepository,
                applicationRepository,
                shelterRepository);
    }

    @Test
    void createApplicationPersistsNewApplicationForAvailableAnimal() {
        Shelter shelter = shelter(SHELTER_ID);
        Animal animal = animal(shelter, AnimalStatus.AVAILABLE);
        when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.of(animal));
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(invocation -> {
            AdoptionApplication application = invocation.getArgument(0);
            application.setId(APPLICATION_ID);
            return application;
        });

        CreateApplicationResponse response = applicationService.createApplication(
                new CreateApplicationRequest(ANIMAL_ID, "  Alex Smith  ", "  +79998887766  "));

        assertThat(response).isEqualTo(new CreateApplicationResponse(
                APPLICATION_ID, ApplicationStatus.NEW));
        var savedApplication = org.mockito.ArgumentCaptor.forClass(AdoptionApplication.class);
        verify(applicationRepository).save(savedApplication.capture());
        AdoptionApplication application = savedApplication.getValue();
        assertThat(application.getAnimal()).isSameAs(animal);
        assertThat(application.getShelter()).isSameAs(shelter);
        assertThat(application.getApplicantName()).isEqualTo("Alex Smith");
        assertThat(application.getApplicantPhone()).isEqualTo("+79998887766");
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.NEW);
    }

    @Test
    void createApplicationRejectsNullRequest() {
        assertBadRequest(() -> applicationService.createApplication(null));
        verifyNoInteractions(animalRepository, applicationRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidAnimalIds")
    void createApplicationRejectsMissingOrNonPositiveAnimalId(Long animalId) {
        assertBadRequest(() -> applicationService.createApplication(
                new CreateApplicationRequest(animalId, "Alex Smith", "+79998887766")));
        verifyNoInteractions(animalRepository, applicationRepository);
    }

    private static Stream<Arguments> invalidAnimalIds() {
        return Stream.of(Arguments.of((Long) null), Arguments.of(0L), Arguments.of(-1L));
    }

    @ParameterizedTest
    @MethodSource("invalidApplicantData")
    void createApplicationRejectsMissingOrOutOfRangeApplicantData(
            CreateApplicationRequest request) {
        assertBadRequest(() -> applicationService.createApplication(request));
        verifyNoInteractions(animalRepository, applicationRepository);
    }

    private static Stream<Arguments> invalidApplicantData() {
        return Stream.of(
                Arguments.of(new CreateApplicationRequest(ANIMAL_ID, null, "+79998887766")),
                Arguments.of(new CreateApplicationRequest(ANIMAL_ID, "A", "+79998887766")),
                Arguments.of(new CreateApplicationRequest(
                        ANIMAL_ID, "N".repeat(129), "+79998887766")),
                Arguments.of(new CreateApplicationRequest(ANIMAL_ID, "Alex Smith", null)),
                Arguments.of(new CreateApplicationRequest(ANIMAL_ID, "Alex Smith", "123456")),
                Arguments.of(new CreateApplicationRequest(
                        ANIMAL_ID, "Alex Smith", "1".repeat(19))));
    }

    @Test
    void createApplicationAcceptsMaximumApplicantFieldLengths() {
        Shelter shelter = shelter(SHELTER_ID);
        Animal animal = animal(shelter, AnimalStatus.AVAILABLE);
        when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.of(animal));
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(invocation -> {
            AdoptionApplication application = invocation.getArgument(0);
            application.setId(APPLICATION_ID);
            return application;
        });

        CreateApplicationResponse response = applicationService.createApplication(
                new CreateApplicationRequest(ANIMAL_ID, "N".repeat(128), "1".repeat(18)));

        assertThat(response.status()).isEqualTo(ApplicationStatus.NEW);
        var savedApplication = org.mockito.ArgumentCaptor.forClass(AdoptionApplication.class);
        verify(applicationRepository).save(savedApplication.capture());
        assertThat(savedApplication.getValue().getApplicantName()).hasSize(128);
        assertThat(savedApplication.getValue().getApplicantPhone()).hasSize(18);
    }

    @Test
    void createApplicationAcceptsMinimumApplicantFieldLengths() {
        Animal animal = animal(shelter(SHELTER_ID), AnimalStatus.AVAILABLE);
        when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.of(animal));
        when(applicationRepository.save(any(AdoptionApplication.class))).thenAnswer(invocation -> {
            AdoptionApplication application = invocation.getArgument(0);
            application.setId(APPLICATION_ID);
            return application;
        });

        CreateApplicationResponse response = applicationService.createApplication(
                new CreateApplicationRequest(ANIMAL_ID, "NN", "1234567"));

        assertThat(response.status()).isEqualTo(ApplicationStatus.NEW);
        var savedApplication = org.mockito.ArgumentCaptor.forClass(AdoptionApplication.class);
        verify(applicationRepository).save(savedApplication.capture());
        assertThat(savedApplication.getValue().getApplicantName()).hasSize(2);
        assertThat(savedApplication.getValue().getApplicantPhone()).hasSize(7);
    }

    @Test
    void createApplicationReturnsNotFoundWhenAnimalDoesNotExist() {
        when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> applicationService.createApplication(
                new CreateApplicationRequest(ANIMAL_ID, "Alex Smith", "+79998887766")));

        verifyNoInteractions(applicationRepository);
    }

    @ParameterizedTest
    @EnumSource(value = AnimalStatus.class, names = {"HIDDEN", "ADOPTED"})
    void createApplicationRejectsAnimalThatIsNotAvailable(AnimalStatus status) {
        when(animalRepository.findById(ANIMAL_ID))
                .thenReturn(Optional.of(animal(shelter(SHELTER_ID), status)));

        assertStatus(HttpStatus.CONFLICT, () -> applicationService.createApplication(
                new CreateApplicationRequest(ANIMAL_ID, "Alex Smith", "+79998887766")));

        verifyNoInteractions(applicationRepository);
    }

    @ParameterizedTest
    @EnumSource(ApplicationStatus.class)
    void changeStatusUpdatesApplicationOwnedByCurrentShelter(ApplicationStatus status) {
        Shelter shelter = shelter(SHELTER_ID);
        Animal animal = animal(shelter, AnimalStatus.AVAILABLE);
        AdoptionApplication application = application(animal, ApplicationStatus.NEW);
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));

        UpdateApplicationStatusResponse response = applicationService.changeStatus(
                principal(),
                APPLICATION_ID,
                new UpdateApplicationStatusRequest(status));

        assertThat(application.getStatus()).isEqualTo(status);
        assertThat(response).isEqualTo(new UpdateApplicationStatusResponse(
                APPLICATION_ID,
                ANIMAL_ID,
                "Alex Smith",
                "+79998887766",
                status));
    }

    @ParameterizedTest
    @MethodSource("unauthenticatedPrincipals")
    void changeStatusRejectsMissingOrBlankPrincipal(Principal principal) {
        assertStatus(HttpStatus.UNAUTHORIZED, () -> applicationService.changeStatus(
                principal,
                APPLICATION_ID,
                new UpdateApplicationStatusRequest(ApplicationStatus.IN_PROGRESS)));
        verifyNoInteractions(shelterRepository, applicationRepository);
    }

    private static Stream<Arguments> unauthenticatedPrincipals() {
        return Stream.of(
                Arguments.of((Principal) null),
                Arguments.of((Principal) () -> null),
                Arguments.of((Principal) () -> ""),
                Arguments.of((Principal) () -> "   "));
    }

    @ParameterizedTest
    @MethodSource("invalidApplicationIds")
    void changeStatusRejectsMissingOrNonPositiveApplicationId(Long applicationId) {
        assertBadRequest(() -> applicationService.changeStatus(
                principal(),
                applicationId,
                new UpdateApplicationStatusRequest(ApplicationStatus.IN_PROGRESS)));
        verifyNoInteractions(shelterRepository, applicationRepository);
    }

    private static Stream<Arguments> invalidApplicationIds() {
        return Stream.of(Arguments.of((Long) null), Arguments.of(0L), Arguments.of(-1L));
    }

    @ParameterizedTest
    @MethodSource("invalidStatusRequests")
    void changeStatusRejectsMissingRequestOrStatus(UpdateApplicationStatusRequest request) {
        assertBadRequest(() -> applicationService.changeStatus(
                principal(), APPLICATION_ID, request));
        verifyNoInteractions(shelterRepository, applicationRepository);
    }

    private static Stream<Arguments> invalidStatusRequests() {
        return Stream.of(
                Arguments.of((UpdateApplicationStatusRequest) null),
                Arguments.of(new UpdateApplicationStatusRequest(null)));
    }

    @Test
    void changeStatusRejectsAccountThatIsNotAShelter() {
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.empty());

        assertStatus(HttpStatus.FORBIDDEN, () -> applicationService.changeStatus(
                principal(),
                APPLICATION_ID,
                new UpdateApplicationStatusRequest(ApplicationStatus.IN_PROGRESS)));

        verifyNoInteractions(applicationRepository);
    }

    @Test
    void changeStatusReturnsNotFoundWhenApplicationDoesNotExist() {
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter(SHELTER_ID)));
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> applicationService.changeStatus(
                principal(),
                APPLICATION_ID,
                new UpdateApplicationStatusRequest(ApplicationStatus.IN_PROGRESS)));
    }

    @Test
    void changeStatusRejectsApplicationOwnedByAnotherShelter() {
        Shelter otherShelter = shelter(99L);
        Animal animal = animal(otherShelter, AnimalStatus.AVAILABLE);
        AdoptionApplication application = application(animal, ApplicationStatus.NEW);
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter(SHELTER_ID)));
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));

        assertStatus(HttpStatus.FORBIDDEN, () -> applicationService.changeStatus(
                principal(),
                APPLICATION_ID,
                new UpdateApplicationStatusRequest(ApplicationStatus.IN_PROGRESS)));

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.NEW);
    }

    @Test
    void findByShelterReturnsApplicationAndAnimalDetailsWithoutStatusFilter() {
        Animal animal = animal(shelter(SHELTER_ID), AnimalStatus.AVAILABLE);
        animal.setName("Rex");
        AdoptionApplication application = application(animal, ApplicationStatus.NEW);
        when(applicationRepository.findShelterApplications(SHELTER_ID))
                .thenReturn(List.of(application));

        List<ApplicationResponse> responses = applicationService.findByShelter(SHELTER_ID, null);

        assertThat(responses).containsExactly(new ApplicationResponse(
                APPLICATION_ID,
                ApplicationStatus.NEW,
                "Alex Smith",
                "+79998887766",
                ANIMAL_ID,
                "Rex"));
        verify(applicationRepository).findShelterApplications(SHELTER_ID);
    }

    @ParameterizedTest
    @EnumSource(ApplicationStatus.class)
    void findByShelterAppliesStatusFilter(ApplicationStatus status) {
        when(applicationRepository.findShelterApplicationsByStatus(SHELTER_ID, status))
                .thenReturn(List.of());

        List<ApplicationResponse> responses = applicationService.findByShelter(SHELTER_ID, status);

        assertThat(responses).isEmpty();
        verify(applicationRepository).findShelterApplicationsByStatus(SHELTER_ID, status);
    }

    private Principal principal() {
        return () -> SHELTER_EMAIL;
    }

    private Shelter shelter(Long id) {
        Shelter shelter = new Shelter();
        shelter.setId(id);
        shelter.setEmail(id.equals(SHELTER_ID) ? SHELTER_EMAIL : "other@example.com");
        return shelter;
    }

    private Animal animal(Shelter shelter, AnimalStatus status) {
        Animal animal = new Animal();
        animal.setId(ANIMAL_ID);
        animal.setShelter(shelter);
        animal.setName("Rex");
        animal.setSpecies(Species.DOG);
        animal.setStatus(status);
        return animal;
    }

    private AdoptionApplication application(Animal animal, ApplicationStatus status) {
        AdoptionApplication application = new AdoptionApplication();
        application.setId(APPLICATION_ID);
        application.setAnimal(animal);
        application.setShelter(animal.getShelter());
        application.setApplicantName("Alex Smith");
        application.setApplicantPhone("+79998887766");
        application.setStatus(status);
        return application;
    }

    private void assertBadRequest(Runnable action) {
        assertStatus(HttpStatus.BAD_REQUEST, action);
    }

    private void assertStatus(HttpStatus expectedStatus, Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(expectedStatus.value()));
    }
}
