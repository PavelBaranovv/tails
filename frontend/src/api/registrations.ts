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

const registrations: ShelterRegistration[] = [
  {
    id: 1,
    name: "Тихий двор",
    district: "Калининский",
    address: "проспект Науки, 15",
    contactName: "Ольга Соколова",
    phone: "+79993334455",
    email: "tihiy@tails.local",
    password: "password123",
  },
]

export function listShelterRegistrations() {
  return registrations.map((request) => ({ ...request }))
}

export function createShelterRegistration(input: Omit<ShelterRegistration, "id">) {
  const registration: ShelterRegistration = {
    id: Math.max(0, ...registrations.map((item) => item.id)) + 1,
    ...input,
  }
  registrations.push(registration)
  return registration
}

export function takeShelterRegistration(id: number) {
  const index = registrations.findIndex((request) => request.id === id)
  if (index < 0) {
    return null
  }
  const [request] = registrations.splice(index, 1)
  return request
}
