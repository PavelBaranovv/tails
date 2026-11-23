export type Species = "CAT" | "DOG" | "PARROT" | "OTHER"
export type Sex = "MALE" | "FEMALE"

export type AnimalCard = {
  id: number
  name: string
  species: Species
  sex: Sex | null
  birthMonth: number | null
  birthYear: number | null
  district: string
  photoUrl: string
}

export type AnimalFilters = {
  district: string
  species: Species | ""
  minAge: number | null
  maxAge: number | null
}

export const DISTRICTS = [
  "Адмиралтейский",
  "Василеостровский",
  "Выборгский",
  "Калининский",
  "Кировский",
  "Колпинский",
  "Красногвардейский",
  "Красносельский",
  "Кронштадтский",
  "Курортный",
  "Московский",
  "Невский",
  "Петроградский",
  "Петродворцовый",
  "Приморский",
  "Пушкинский",
  "Фрунзенский",
  "Центральный",
]

export const SPECIES: { value: Species; label: string }[] = [
  { value: "CAT", label: "Кошка" },
  { value: "DOG", label: "Собака" },
  { value: "PARROT", label: "Попугай" },
  { value: "OTHER", label: "Другое" },
]

export const AGE_OPTIONS: { label: string; minAge: number | null; maxAge: number | null }[] = [
  { label: "Любой", minAge: null, maxAge: null },
  { label: "До 1 года", minAge: null, maxAge: 11 },
  { label: "1–3 года", minAge: 12, maxAge: 35 },
  { label: "Старше 3 лет", minAge: 36, maxAge: null },
]

const animals: AnimalCard[] = [
  {
    id: 1,
    name: "Барсик",
    species: "CAT",
    sex: "MALE",
    birthMonth: 5,
    birthYear: 2024,
    district: "Центральный",
    photoUrl: "/photos/cat.jpg",
  },
  {
    id: 2,
    name: "Мурка",
    species: "CAT",
    sex: "FEMALE",
    birthMonth: null,
    birthYear: null,
    district: "Василеостровский",
    photoUrl: "/photos/cat.jpg",
  },
  {
    id: 3,
    name: "Шарик",
    species: "DOG",
    sex: "MALE",
    birthMonth: 1,
    birthYear: 2020,
    district: "Невский",
    photoUrl: "/photos/dog.jpg",
  },
  {
    id: 4,
    name: "Рекс",
    species: "DOG",
    sex: null,
    birthMonth: 11,
    birthYear: 2023,
    district: "Приморский",
    photoUrl: "/photos/dog.jpg",
  },
  {
    id: 5,
    name: "Кеша",
    species: "PARROT",
    sex: "MALE",
    birthMonth: 3,
    birthYear: 2022,
    district: "Петроградский",
    photoUrl: "/photos/parrot.jpg",
  },
  {
    id: 6,
    name: "Кузя",
    species: "OTHER",
    sex: null,
    birthMonth: null,
    birthYear: null,
    district: "Калининский",
    photoUrl: "/photos/other.png",
  },
  {
    id: 7,
    name: "Лайка",
    species: "DOG",
    sex: "FEMALE",
    birthMonth: 8,
    birthYear: 2025,
    district: "Московский",
    photoUrl: "/photos/dog.jpg",
  },
  {
    id: 8,
    name: "Снежок",
    species: "CAT",
    sex: "FEMALE",
    birthMonth: 2,
    birthYear: 2019,
    district: "Пушкинский",
    photoUrl: "/photos/cat.jpg",
  },
]

export function speciesLabel(species: Species) {
  return SPECIES.find((item) => item.value === species)?.label ?? species
}

export function sexLabel(sex: Sex) {
  return sex === "MALE" ? "Самец" : "Самка"
}

export function ageInMonths(animal: AnimalCard, today = new Date()) {
  if (animal.birthYear == null || animal.birthMonth == null) {
    return null
  }
  const months =
    (today.getFullYear() - animal.birthYear) * 12 +
    (today.getMonth() + 1 - animal.birthMonth)
  return Math.max(months, 0)
}

export function formatAge(months: number) {
  const years = Math.floor(months / 12)
  const rest = months % 12
  if (years === 0) {
    return `${rest} мес.`
  }
  const mod10 = years % 10
  const mod100 = years % 100
  const yearWord =
    mod10 === 1 && mod100 !== 11
      ? "год"
      : mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)
        ? "года"
        : "лет"
  if (rest === 0) {
    return `${years} ${yearWord}`
  }
  return `${years} ${yearWord} ${rest} мес.`
}

export function getAnimals(filters: AnimalFilters) {
  return animals.filter((animal) => {
    if (filters.district && animal.district !== filters.district) {
      return false
    }
    if (filters.species && animal.species !== filters.species) {
      return false
    }
    if (filters.minAge != null || filters.maxAge != null) {
      const age = ageInMonths(animal)
      if (age == null) {
        return false
      }
      if (filters.minAge != null && age < filters.minAge) {
        return false
      }
      if (filters.maxAge != null && age > filters.maxAge) {
        return false
      }
    }
    return true
  })
}
