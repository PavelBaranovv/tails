import { Navigate, NavLink, useParams } from "react-router-dom"
import SiteHeader from "./SiteHeader.tsx"
import ShelterPets from "./ShelterPets.tsx"
import "./ShelterCabinetPage.css"

const sections = [
  { id: "pets", label: "Питомцы" },
  { id: "applications", label: "Заявки" },
  { id: "profile", label: "Профиль" },
] as const

function ShelterCabinetPage() {
  const params = useParams()
  const current = sections.find((section) => section.id === params.section)

  if (!current) {
    return <Navigate to="/shelter/pets" replace />
  }

  return (
    <div className="cabinet-page">
      <SiteHeader />
      <main className="cabinet">
        <nav className="cabinet__tabs">
          {sections.map((section) => (
            <NavLink
              key={section.id}
              to={`/shelter/${section.id}`}
              className={({ isActive }) =>
                isActive ? "cabinet__tab cabinet__tab--active" : "cabinet__tab"
              }
            >
              {section.label}
            </NavLink>
          ))}
        </nav>
        {current.id === "pets" ? (
          <ShelterPets />
        ) : (
          <section className="cabinet__panel" aria-label={current.label} />
        )}
      </main>
    </div>
  )
}

export default ShelterCabinetPage
