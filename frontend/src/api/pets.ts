import { sexLabel, speciesLabel, SPECIES, type Sex, type Species } from "./animals"

export type PetStatus = "HIDDEN" | "AVAILABLE" | "ADOPTED"

export type ShelterPet = {
  id: number
  photoUrl: string | null
  species: Species
  speciesOther: string | null
  name: string
  age: string
  sex: Sex | null
  breed: string | null
  status: PetStatus
}

export const PET_STATUSES: { value: PetStatus; label: string }[] = [
  { value: "AVAILABLE", label: "Доступен" },
  { value: "HIDDEN", label: "Скрыт" },
  { value: "ADOPTED", label: "Приютили" },
]

export function petStatusLabel(status: PetStatus, sex: Sex | null) {
  if (status === "AVAILABLE" && sex === "FEMALE") {
    return "Доступна"
  }
  return PET_STATUSES.find((item) => item.value === status)?.label ?? status
}

export function petSpeciesLabel(pet: ShelterPet) {
  if (pet.species === "OTHER" && pet.speciesOther) {
    return pet.speciesOther
  }
  return speciesLabel(pet.species, pet.sex)
}

export function petSexLabel(sex: Sex | null) {
  return sex == null ? "Пол неизвестен" : sexLabel(sex)
}

export function petBreedLabel(breed: string | null) {
  return breed == null ? "Порода неизвестна" : breed
}

const pets: ShelterPet[] = [
  {
    id: 1,
    photoUrl: "/photos/cat.jpg",
    species: "CAT",
    speciesOther: null,
    name: "Барсик",
    age: "2 года 6 месяцев",
    sex: "MALE",
    breed: "Британская",
    status: "AVAILABLE",
  },
  {
    id: 2,
    photoUrl: "/photos/cat.jpg",
    species: "CAT",
    speciesOther: null,
    name: "Мурка",
    age: "Возраст неизвестен",
    sex: "FEMALE",
    breed: null,
    status: "AVAILABLE",
  },
  {
    id: 3,
    photoUrl: "/photos/dog.jpg",
    species: "DOG",
    speciesOther: null,
    name: "Шарик",
    age: "6 лет 10 месяцев",
    sex: "MALE",
    breed: "Дворняга",
    status: "HIDDEN",
  },
  {
    id: 4,
    photoUrl: "/photos/dog.jpg",
    species: "DOG",
    speciesOther: null,
    name: "Рекс",
    age: "3 года",
    sex: null,
    breed: "Овчарка",
    status: "ADOPTED",
  },
  {
    id: 5,
    photoUrl: "/photos/parrot.jpg",
    species: "PARROT",
    speciesOther: null,
    name: "Кеша",
    age: "4 года 8 месяцев",
    sex: "MALE",
    breed: "Волнистый",
    status: "AVAILABLE",
  },
]

export function listShelterPets(status: PetStatus | "ALL", species: Species | "") {
  return pets.filter(
    (pet) => (status === "ALL" || pet.status === status) && (species === "" || pet.species === species),
  )
}

export function markPetAdopted(id: number) {
  const pet = pets.find((item) => item.id === id)
  if (pet) {
    pet.status = "ADOPTED"
  }
}

export { SPECIES }
