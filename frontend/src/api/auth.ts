const SHELTER_EMAIL = "shelter@tails.local"
const ADMIN_EMAIL = "admin@tails.local"
const ACCOUNT_PASSWORD = "password123"
const SESSION_KEY = "tails.shelter"

export const SHELTER_NAME = "Добрые лапы"

export type AccountRole = "SHELTER" | "ADMIN"

export type ShelterSession = {
  name: string
  email: string
  role: AccountRole
}

export function loginShelter(email: string, password: string) {
  const normalized = email.trim().toLowerCase()
  const session: ShelterSession | null =
    normalized === ADMIN_EMAIL && password === ACCOUNT_PASSWORD
      ? { name: "Администратор", email: ADMIN_EMAIL, role: "ADMIN" }
      : normalized === SHELTER_EMAIL && password === ACCOUNT_PASSWORD
        ? { name: SHELTER_NAME, email: SHELTER_EMAIL, role: "SHELTER" }
        : null
  if (!session) {
    return null
  }
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session))
  return session.role
}

export function getShelterSession(): ShelterSession | null {
  const raw = sessionStorage.getItem(SESSION_KEY)
  if (!raw) {
    return null
  }
  try {
    const parsed = JSON.parse(raw) as Partial<ShelterSession>
    if (!parsed.name || !parsed.email) {
      return null
    }
    return {
      name: parsed.name,
      email: parsed.email,
      role: parsed.role === "ADMIN" ? "ADMIN" : "SHELTER",
    }
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
