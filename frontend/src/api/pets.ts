import { sexLabel, speciesLabel, SPECIES, type Sex, type Species } from "./animals"

export type PetStatus = "HIDDEN" | "AVAILABLE" | "ADOPTED"

export type ShelterPet = {
  id: number
  photoUrls: string[]
  species: Species
  speciesOther: string | null
  name: string
  birthMonth: number | null
  birthYear: number | null
  sex: Sex | null
  breed: string | null
  description: string
  status: PetStatus
}

export type PetInput = Omit<ShelterPet, "id">

export type ShelterPetView = ShelterPet & { age: string }

export const PET_STATUSES: { value: PetStatus; label: string }[] = [
  { value: "AVAILABLE", label: "Доступен" },
  { value: "HIDDEN", label: "Скрыт" },
  { value: "ADOPTED", label: "Приютили" },
]

export const MONTHS = [
  "Январь",
  "Февраль",
  "Март",
  "Апрель",
  "Май",
  "Июнь",
  "Июль",
  "Август",
  "Сентябрь",
  "Октябрь",
  "Ноябрь",
  "Декабрь",
]

export function petStatusLabel(status: PetStatus, sex: Sex | null) {
  if (status === "AVAILABLE" && sex === "FEMALE") {
    return "Доступна"
  }
  return PET_STATUSES.find((item) => item.value === status)?.label ?? status
}

export function petSpeciesLabel(pet: Pick<ShelterPet, "species" | "speciesOther" | "sex">) {
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

function word(count: number, one: string, few: string, many: string) {
  const lastTwo = count % 100
  if (lastTwo >= 11 && lastTwo <= 14) {
    return many
  }
  switch (count % 10) {
    case 1:
      return one
    case 2:
    case 3:
    case 4:
      return few
    default:
      return many
  }
}

export function formatPetAge(birthMonth: number | null, birthYear: number | null) {
  if (birthYear == null) {
    return "Возраст неизвестен"
  }

  const now = new Date()
  const currentYear = now.getFullYear()
  const currentMonth = now.getMonth() + 1
  if (birthMonth == null) {
    const years = currentYear - birthYear
    return years < 0 ? "Возраст неизвестен" : `${years} ${word(years, "год", "года", "лет")}`
  }

  const totalMonths = (currentYear - birthYear) * 12 + (currentMonth - birthMonth)
  if (totalMonths < 0) {
    return "Возраст неизвестен"
  }
  const years = Math.floor(totalMonths / 12)
  const months = totalMonths % 12
  if (years === 0) {
    return `${months} ${word(months, "месяц", "месяца", "месяцев")}`
  }
  if (months === 0) {
    return `${years} ${word(years, "год", "года", "лет")}`
  }
  return `${years} ${word(years, "год", "года", "лет")} ${months} ${word(months, "месяц", "месяца", "месяцев")}`
}

const pets: ShelterPet[] = [
  {
    id: 1,
    photoUrls: ["/photos/cat.jpg"],
    species: "CAT",
    speciesOther: null,
    name: "Барсик",
    birthMonth: 5,
    birthYear: 2024,
    sex: "MALE",
    breed: "Британская",
    description: "Спокойный кот, любит сидеть на коленях.",
    status: "AVAILABLE",
  },
  {
    id: 2,
    photoUrls: ["/photos/cat.jpg"],
    species: "CAT",
    speciesOther: null,
    name: "Мурка",
    birthMonth: null,
    birthYear: null,
    sex: "FEMALE",
    breed: null,
    description: "",
    status: "AVAILABLE",
  },
  {
    id: 3,
    photoUrls: ["/photos/dog.jpg"],
    species: "DOG",
    speciesOther: null,
    name: "Шарик",
    birthMonth: 1,
    birthYear: 2020,
    sex: "MALE",
    breed: "Дворняга",
    description: "Активный пес, хорошо гуляет на поводке.",
    status: "HIDDEN",
  },
  {
    id: 4,
    photoUrls: ["/photos/dog.jpg"],
    species: "DOG",
    speciesOther: null,
    name: "Рекс",
    birthMonth: 11,
    birthYear: 2023,
    sex: null,
    breed: "Овчарка",
    description: "Уже живет в новой семье.",
    status: "ADOPTED",
  },
  {
    id: 5,
    photoUrls: ["/photos/parrot.jpg"],
    species: "PARROT",
    speciesOther: null,
    name: "Кеша",
    birthMonth: 3,
    birthYear: 2022,
    sex: "MALE",
    breed: "Волнистый",
    description: "Говорит несколько слов и любит яблоки.",
    status: "AVAILABLE",
  },
]

function withAge(pet: ShelterPet): ShelterPetView {
  return { ...pet, age: formatPetAge(pet.birthMonth, pet.birthYear) }
}

export function listShelterPets(status: PetStatus | "ALL", species: Species | "") {
  return pets
    .filter((pet) => (status === "ALL" || pet.status === status) && (species === "" || pet.species === species))
    .map(withAge)
}

export function getPet(id: number) {
  const pet = pets.find((item) => item.id === id)
  return pet ? withAge(pet) : null
}

export function createPet(input: PetInput) {
  const pet: ShelterPet = { id: Math.max(...pets.map((item) => item.id)) + 1, ...input }
  pets.push(pet)
  return withAge(pet)
}

export function updatePet(id: number, input: PetInput) {
  const pet = pets.find((item) => item.id === id)
  if (!pet) {
    return null
  }
  Object.assign(pet, input)
  return withAge(pet)
}

export function deletePet(id: number) {
  const index = pets.findIndex((item) => item.id === id)
  if (index >= 0) {
    pets.splice(index, 1)
  }
}

export function markPetAdopted(id: number) {
  const pet = pets.find((item) => item.id === id)
  if (pet) {
    pet.status = "ADOPTED"
  }
}

export { SPECIES }
