const SHELTER_EMAIL = "shelter@tails.local"
const SHELTER_PASSWORD = "password123"
const SESSION_KEY = "tails.shelter"

export const SHELTER_NAME = "Добрые лапы"

export type ShelterSession = {
  name: string
  email: string
}

export function loginShelter(email: string, password: string) {
  const matches = email.trim().toLowerCase() === SHELTER_EMAIL && password === SHELTER_PASSWORD
  if (!matches) {
    return false
  }
  const session: ShelterSession = { name: SHELTER_NAME, email: SHELTER_EMAIL }
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session))
  return true
}

export function getShelterSession(): ShelterSession | null {
  const raw = sessionStorage.getItem(SESSION_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as ShelterSession
  } catch {
    return null
  }
}

export function logoutShelter() {
  sessionStorage.removeItem(SESSION_KEY)
}

export function updateShelterSessionName(name: string) {
  const session = getShelterSession()
  if (!session) {
    return
  }
  sessionStorage.setItem(SESSION_KEY, JSON.stringify({ ...session, name }))
  window.dispatchEvent(new Event("tails-shelter-session"))
}
