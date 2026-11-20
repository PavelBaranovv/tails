package ru.spbpu.tails.shelter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.YearMonth;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalPhoto;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.entity.District;
import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.Species;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ApplicationRepository;
import ru.spbpu.tails.common.repository.DistrictRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {

    private static final String SHELTER_EMAIL = "shelter@example.com";
    private static final Long SHELTER_ID = 42L;

    @Mock
    private ShelterRepository shelterRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    private ShelterService shelterService;

    @BeforeEach
    void setUp() {
        shelterService = new ShelterService(
                shelterRepository,
                districtRepository,
                animalRepository,
                applicationRepository);
    }

    @Test
    void getProfileReturnsCurrentShelterProfile() {
        Shelter shelter = shelter();
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));

        ShelterProfile profile = shelterService.getProfile(principal());

        assertThat(profile).isEqualTo(new ShelterProfile(
                "Happy Tails",
                "Central",
                "10 Main Street",
                "+79991234567"));
    }

    @Test
    void getProfileRejectsMissingPrincipal() {
        assertThatThrownBy(() -> shelterService.getProfile(null))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        verifyNoInteractions(shelterRepository);
    }

    @Test
    void getProfileRejectsPrincipalWithoutName() {
        Principal principalWithoutName = () -> null;

        assertThatThrownBy(() -> shelterService.getProfile(principalWithoutName))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        verifyNoInteractions(shelterRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void getProfileRejectsBlankPrincipalName(String name) {
        Principal principalWithBlankName = () -> name;

        assertThatThrownBy(() -> shelterService.getProfile(principalWithBlankName))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        verifyNoInteractions(shelterRepository);
    }

    @Test
    void getProfileRejectsAccountThatIsNotAShelter() {
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shelterService.getProfile(principal()))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    void updateProfileTrimsValuesAndUpdatesShelter() {
        Shelter shelter = shelter();
        District district = district("North");
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(districtRepository.findByNameIgnoreCase("North"))
                .thenReturn(Optional.of(district));

        ShelterProfile profile = shelterService.updateProfile(
                principal(),
                new UpdateShelterProfileRequest(
                        "  New Shelter  ",
                        "  North  ",
                        "  20 New Street  ",
                        "  +79990001122  "));

        assertThat(profile).isEqualTo(new ShelterProfile(
                "New Shelter",
                "North",
                "20 New Street",
                "+79990001122"));
        assertThat(shelter.getName()).isEqualTo("New Shelter");
        assertThat(shelter.getDistrict()).isSameAs(district);
        assertThat(shelter.getAddress()).isEqualTo("20 New Street");
        assertThat(shelter.getPhone()).isEqualTo("+79990001122");
    }

    @Test
    void updateProfileRejectsInvalidNameBeforeRepositoryAccess() {
        UpdateShelterProfileRequest request = new UpdateShelterProfileRequest(
                " ", "Central", "10 Main Street", "+79991234567");

        assertThatThrownBy(() -> shelterService.updateProfile(principal(), request))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(shelterRepository, districtRepository);
    }

    @Test
    void updateProfileRejectsNullRequest() {
        assertThatThrownBy(() -> shelterService.updateProfile(principal(), null))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(shelterRepository, districtRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidProfileRequests")
    void updateProfileRejectsMissingOrTooShortFields(UpdateShelterProfileRequest request) {
        assertThatThrownBy(() -> shelterService.updateProfile(principal(), request))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(shelterRepository, districtRepository);
    }

    private static Stream<Arguments> invalidProfileRequests() {
        return Stream.of(
                Arguments.of(new UpdateShelterProfileRequest(
                        null, "Central", "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", null, "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", null, "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", "10 Main Street", null)),
                Arguments.of(new UpdateShelterProfileRequest(
                        "A", "Central", "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "", "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", "A", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", "10 Main Street", "123456")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "N".repeat(257), "Central", "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "D".repeat(129), "10 Main Street", "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", "A".repeat(257), "+79991234567")),
                Arguments.of(new UpdateShelterProfileRequest(
                        "Happy Tails", "Central", "10 Main Street", "1".repeat(19))));
    }

    @Test
    void updateProfileAcceptsMinimumFieldLengths() {
        String name = "NN";
        String districtName = "D";
        String address = "AA";
        String phone = "1234567";
        Shelter shelter = shelter();
        District district = district(districtName);
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(districtRepository.findByNameIgnoreCase(districtName))
                .thenReturn(Optional.of(district));

        ShelterProfile profile = shelterService.updateProfile(
                principal(),
                new UpdateShelterProfileRequest(name, districtName, address, phone));

        assertThat(profile).isEqualTo(new ShelterProfile(name, districtName, address, phone));
    }

    @Test
    void updateProfileAcceptsMaximumFieldLengths() {
        String name = "N".repeat(256);
        String districtName = "D".repeat(128);
        String address = "A".repeat(256);
        String phone = "1".repeat(18);
        Shelter shelter = shelter();
        District district = district(districtName);
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(districtRepository.findByNameIgnoreCase(districtName))
                .thenReturn(Optional.of(district));

        ShelterProfile profile = shelterService.updateProfile(
                principal(),
                new UpdateShelterProfileRequest(name, districtName, address, phone));

        assertThat(profile).isEqualTo(new ShelterProfile(name, districtName, address, phone));
    }

    @Test
    void updateProfileRejectsUnknownDistrict() {
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter()));
        when(districtRepository.findByNameIgnoreCase("Unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> shelterService.updateProfile(
                principal(),
                new UpdateShelterProfileRequest(
                        "New Shelter", "Unknown", "20 New Street", "+79990001122")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value())
                                .isEqualTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void getPetsDefaultsToAvailableAndMapsMainPhotoAndPetFields() {
        Shelter shelter = shelter();
        Animal animal = animal(shelter);
        AnimalPhoto mainPhoto = photo("pets/main.jpg", true);
        AnimalPhoto secondaryPhoto = photo("pets/secondary.jpg", false);
        animal.setPhotos(List.of(secondaryPhoto, mainPhoto));
        animal.setSpecies(Species.DOG);
        animal.setSpeciesOther(null);
        animal.setName("Rex");
        animal.setGender(Gender.MALE);
        animal.setBreed("Mixed");
        animal.setBirthMonth((short) YearMonth.now().getMonthValue());
        animal.setBirthYear((short) YearMonth.now().getYear());

        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(animalRepository.findShelterPetsByStatus(SHELTER_ID, AnimalStatus.AVAILABLE))
                .thenReturn(List.of(animal));

        List<ShelterPetResponse> pets = shelterService.getPets(principal(), null, null);

        assertThat(pets).hasSize(1);
        ShelterPetResponse pet = pets.get(0);
        assertThat(pet.id()).isEqualTo(7L);
        assertThat(pet.photoKey()).isEqualTo("pets/main.jpg");
        assertThat(pet.species()).isEqualTo(Species.DOG);
        assertThat(pet.speciesOther()).isNull();
        assertThat(pet.name()).isEqualTo("Rex");
        assertThat(pet.age()).startsWith("0 ");
        assertThat(pet.birthMonth()).isEqualTo((short) YearMonth.now().getMonthValue());
        assertThat(pet.birthYear()).isEqualTo((short) YearMonth.now().getYear());
        assertThat(pet.gender()).isEqualTo(Gender.MALE);
        assertThat(pet.breed()).isEqualTo("Mixed");
        assertThat(pet.status()).isEqualTo(AnimalStatus.AVAILABLE);
        verify(animalRepository).findShelterPetsByStatus(SHELTER_ID, AnimalStatus.AVAILABLE);
    }

    @ParameterizedTest
    @MethodSource("petFilterArguments")
    void getPetsAppliesExplicitStatusAndSpeciesFilters(AnimalStatus status, Species species) {
        Shelter shelter = shelter();
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(animalRepository.findShelterPetsByStatusAndSpecies(
                SHELTER_ID, status, species))
                .thenReturn(List.of());

        List<ShelterPetResponse> pets = shelterService.getPets(
                principal(), status, species);

        assertThat(pets).isEmpty();
        verify(animalRepository).findShelterPetsByStatusAndSpecies(
                SHELTER_ID, status, species);
    }

    private static Stream<Arguments> petFilterArguments() {
        return Stream.of(AnimalStatus.values())
                .flatMap(status -> Stream.of(Species.values())
                        .map(species -> Arguments.of(status, species)));
    }

    @Test
    void getPetsReturnsNullPhotoKeyWhenThereIsNoMainPhoto() {
        Animal animal = animal(shelter());
        animal.setPhotos(List.of(photo("pets/secondary.jpg", false)));

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).photoKey()).isNull();
    }

    @Test
    void getPetsReturnsUnknownAgeWhenBirthYearIsMissing() {
        Animal animal = animal(shelter());
        animal.setBirthMonth(null);
        animal.setBirthYear(null);

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        assertThat(pets.get(0).age()).doesNotMatch("^\\d+\\s.*");
    }

    @Test
    void getPetsReturnsUnknownAgeWhenBirthDateIsInTheFuture() {
        YearMonth futureBirthMonth = YearMonth.now().plusMonths(1);
        Animal animal = animal(shelter());
        animal.setBirthYear((short) futureBirthMonth.getYear());
        animal.setBirthMonth((short) futureBirthMonth.getMonthValue());

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        assertThat(pets.get(0).age()).doesNotMatch("^\\d+\\s.*");
    }

    @Test
    void getPetsReturnsUnknownAgeWhenFutureBirthYearHasNoMonth() {
        Animal animal = animal(shelter());
        animal.setBirthYear((short) (YearMonth.now().getYear() + 1));
        animal.setBirthMonth(null);

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        assertThat(pets.get(0).age()).doesNotMatch("^\\d+\\s.*");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 11, 12, 14, 24, 60, 132, 144, 168, 252, 264, 300, 302})
    void getPetsCalculatesAgeForDifferentYearAndMonthCombinations(int monthsSinceBirth) {
        YearMonth birthMonth = YearMonth.now().minusMonths(monthsSinceBirth);
        Animal animal = animal(shelter());
        animal.setBirthYear((short) birthMonth.getYear());
        animal.setBirthMonth((short) birthMonth.getMonthValue());

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        int years = monthsSinceBirth / 12;
        int months = monthsSinceBirth % 12;
        String expectedLeadingNumber = years == 0 ? Integer.toString(months) : Integer.toString(years);
        assertThat(pets.get(0).age()).startsWith(expectedLeadingNumber + " ");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 11, 12, 14, 21, 22, 25})
    void getPetsCalculatesAgeWhenOnlyBirthYearIsKnown(int yearsSinceBirth) {
        Animal animal = animal(shelter());
        animal.setBirthYear((short) (YearMonth.now().getYear() - yearsSinceBirth));
        animal.setBirthMonth(null);

        List<ShelterPetResponse> pets = getDefaultPets(animal);

        assertThat(pets.get(0).age()).startsWith(yearsSinceBirth + " ");
    }

    @Test
    void getApplicationsReturnsShelterApplicationsAndAnimalDetails() {
        Shelter shelter = shelter();
        Animal animal = animal(shelter);
        animal.setName("Rex");
        AdoptionApplication application = application(animal, ApplicationStatus.NEW);

        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(applicationRepository.findShelterApplications(SHELTER_ID))
                .thenReturn(List.of(application));

        List<ShelterApplicationResponse> applications =
                shelterService.getApplications(principal(), null);

        assertThat(applications).containsExactly(new ShelterApplicationResponse(
                19L,
                ApplicationStatus.NEW,
                "Alex Smith",
                "+79998887766",
                7L,
                "Rex"));
        verify(applicationRepository).findShelterApplications(SHELTER_ID);
    }

    @ParameterizedTest
    @EnumSource(ApplicationStatus.class)
    void getApplicationsPassesStatusFilterToRepository(ApplicationStatus status) {
        Shelter shelter = shelter();
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(shelter));
        when(applicationRepository.findShelterApplicationsByStatus(
                SHELTER_ID, status))
                .thenReturn(List.of());

        List<ShelterApplicationResponse> applications = shelterService.getApplications(
                principal(), status);

        assertThat(applications).isEmpty();
        verify(applicationRepository).findShelterApplicationsByStatus(SHELTER_ID, status);
    }

    private Principal principal() {
        return () -> SHELTER_EMAIL;
    }

    private List<ShelterPetResponse> getDefaultPets(Animal animal) {
        when(shelterRepository.findByEmailIgnoreCase(SHELTER_EMAIL))
                .thenReturn(Optional.of(animal.getShelter()));
        when(animalRepository.findShelterPetsByStatus(SHELTER_ID, AnimalStatus.AVAILABLE))
                .thenReturn(List.of(animal));

        return shelterService.getPets(principal(), null, null);
    }

    private Shelter shelter() {
        Shelter shelter = new Shelter();
        shelter.setId(SHELTER_ID);
        shelter.setName("Happy Tails");
        shelter.setDistrict(district("Central"));
        shelter.setAddress("10 Main Street");
        shelter.setPhone("+79991234567");
        shelter.setEmail(SHELTER_EMAIL);
        return shelter;
    }

    private District district(String name) {
        District district = new District();
        district.setName(name);
        return district;
    }

    private Animal animal(Shelter shelter) {
        Animal animal = new Animal();
        animal.setId(7L);
        animal.setShelter(shelter);
        animal.setName("Rex");
        animal.setSpecies(Species.DOG);
        animal.setStatus(AnimalStatus.AVAILABLE);
        return animal;
    }

    private AnimalPhoto photo(String objectKey, boolean isMain) {
        AnimalPhoto photo = new AnimalPhoto();
        photo.setObjectKey(objectKey);
        photo.setMain(isMain);
        return photo;
    }

    private AdoptionApplication application(Animal animal, ApplicationStatus status) {
        AdoptionApplication application = new AdoptionApplication();
        application.setId(19L);
        application.setAnimal(animal);
        application.setShelter(animal.getShelter());
        application.setApplicantName("Alex Smith");
        application.setApplicantPhone("+79998887766");
        application.setStatus(status);
        return application;
    }
}
