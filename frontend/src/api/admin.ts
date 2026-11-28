import { listShelterPets } from "./pets"
import { listShelterRegistrations, takeShelterRegistration } from "./registrations"

export type AdminShelter = {
  id: number
  name: string
  phone: string
  email: string
  district: string
  address: string
  availableCount: number | null
}

const shelters: AdminShelter[] = [
  {
    id: 1,
    name: "Добрые лапы",
    phone: "+79991234567",
    email: "shelter@tails.local",
    district: "Центральный",
    address: "ул. Примерная, 10",
    availableCount: null,
  },
]

export function listAdminShelters() {
  const available = listShelterPets("AVAILABLE", "").length
  return shelters.map((shelter) =>
    shelter.availableCount == null ? { ...shelter, availableCount: available } : shelter,
  )
}

export function listRegistrationRequests() {
  return listShelterRegistrations()
}

export function approveRegistration(id: number) {
  const request = takeShelterRegistration(id)
  if (!request) {
    return
  }
  shelters.push({
    id: Math.max(0, ...shelters.map((shelter) => shelter.id)) + 1,
    name: request.name,
    phone: request.phone,
    email: request.email,
    district: request.district,
    address: request.address,
    availableCount: 0,
  })
}

export function rejectRegistration(id: number) {
  takeShelterRegistration(id)
}
