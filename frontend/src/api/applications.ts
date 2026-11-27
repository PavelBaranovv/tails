export type Application = {
  id: number
  animalId: number
  applicantName: string
  applicantPhone: string
  status: "NEW"
}

const applications: Application[] = []

export function createApplication(input: {
  animalId: number
  applicantName: string
  applicantPhone: string
}) {
  const application: Application = {
    id: applications.length + 1,
    status: "NEW",
    ...input,
  }
  applications.push(application)
  return application
}
