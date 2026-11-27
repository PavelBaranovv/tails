export type ShelterProfile = {
  name: string
  district: string
  address: string
  phone: string
}

let profile: ShelterProfile = {
  name: "Добрые лапы",
  district: "Центральный",
  address: "ул. Примерная, 10",
  phone: "+79991234567",
}

export function getShelterProfile(): ShelterProfile {
  return { ...profile }
}

export function updateShelterProfile(next: ShelterProfile) {
  profile = { ...next }
  return getShelterProfile()
}
