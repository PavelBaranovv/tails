export type ShelterRegistration = {
  id: number
  name: string
  district: string
  address: string
  contactName: string
  phone: string
  email: string
  password: string
}

const registrations: ShelterRegistration[] = []

export function createShelterRegistration(input: Omit<ShelterRegistration, "id">) {
  const registration: ShelterRegistration = {
    id: registrations.length + 1,
    ...input,
  }
  registrations.push(registration)
  return registration
}
