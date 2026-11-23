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
  breed: string | null
  description: string | null
  shelterName: string
  phone: string
  address: string
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
    breed: "Британская",
    description: "Спокойный и дружелюбный кот.",
    shelterName: "Добрые лапы",
    phone: "+79991234567",
    address: "ул. Примерная, 10",
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
    breed: null,
    description: null,
    shelterName: "Остров надежды",
    phone: "+79990001122",
    address: "Средний проспект, 5",
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
    breed: "Дворняжка",
    description: "Активный пёс, любит прогулки.",
    shelterName: "Невский дом",
    phone: "+79997654321",
    address: "проспект Большевиков, 12",
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
    breed: null,
    description: "Спокойно относится к детям.",
    shelterName: "Приморский приют",
    phone: "+79991112233",
    address: "улица Савушкина, 20",
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
    breed: "Волнистый",
    description: null,
    shelterName: "Крыло",
    phone: "+79995556677",
    address: "Каменноостровский проспект, 8",
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
    breed: null,
    description: null,
    shelterName: "Тихий двор",
    phone: "+79993334455",
    address: "проспект Науки, 15",
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
    breed: "Хаски",
    description: "Молодая и игривая.",
    shelterName: "Север",
    phone: "+79992223344",
    address: "Московский проспект, 100",
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
    breed: "Сибирская",
    description: "Ласковый кот, привык к дому.",
    shelterName: "Пушкинский приют",
    phone: "+79994445566",
    address: "улица Архитектора Данини, 3",
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

export function getAnimal(id: number) {
  return animals.find((animal) => animal.id === id) ?? null
}
