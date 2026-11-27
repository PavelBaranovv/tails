import { getAnimal } from "./animals"

export type ApplicationStatus = "NEW" | "IN_PROGRESS" | "PROCESSED"

export type Application = {
  id: number
  animalId: number
  animalName: string
  applicantName: string
  applicantPhone: string
  status: ApplicationStatus
}

export const APPLICATION_STATUSES: { value: ApplicationStatus; label: string }[] = [
  { value: "NEW", label: "Не обработана" },
  { value: "IN_PROGRESS", label: "В работе" },
  { value: "PROCESSED", label: "Обработана" },
]

export function applicationStatusLabel(status: ApplicationStatus) {
  return APPLICATION_STATUSES.find((item) => item.value === status)?.label ?? status
}

const applications: Application[] = [
  {
    id: 1,
    animalId: 1,
    animalName: "Барсик",
    applicantName: "Анна Иванова",
    applicantPhone: "+79991112233",
    status: "NEW",
  },
  {
    id: 2,
    animalId: 7,
    animalName: "Лайка",
    applicantName: "Пётр Смирнов",
    applicantPhone: "+79992223344",
    status: "IN_PROGRESS",
  },
  {
    id: 3,
    animalId: 5,
    animalName: "Кеша",
    applicantName: "Мария Кузнецова",
    applicantPhone: "+79993334455",
    status: "PROCESSED",
  },
]

export function createApplication(input: {
  animalId: number
  applicantName: string
  applicantPhone: string
}) {
  const application: Application = {
    id: Math.max(0, ...applications.map((item) => item.id)) + 1,
    animalName: getAnimal(input.animalId)?.name ?? "Питомец",
    status: "NEW",
    ...input,
  }
  applications.push(application)
  return application
}

export function listShelterApplications(status: ApplicationStatus | "ALL") {
  return applications.filter((item) => status === "ALL" || item.status === status)
}

export function updateApplicationStatus(id: number, status: ApplicationStatus) {
  const application = applications.find((item) => item.id === id)
  if (application) {
    application.status = status
  }
}
