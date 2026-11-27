import { useState } from "react"
import { Navigate, NavLink, useParams } from "react-router-dom"
import {
  approveRegistration,
  listAdminShelters,
  listRegistrationRequests,
  rejectRegistration,
} from "../api/admin"
import SiteHeader from "./SiteHeader.tsx"
import "./ShelterCabinetPage.css"
import "./AdminPage.css"

const sections = [
  { id: "shelters", label: "Приюты" },
  { id: "requests", label: "Заявки на регистрацию" },
] as const

function AdminPage() {
  const params = useParams()
  const current = sections.find((section) => section.id === params.section)
  const [version, setVersion] = useState(0)

  if (!current) {
    return <Navigate to="/admin/shelters" replace />
  }

  const shelters = listAdminShelters()
  const requests = listRegistrationRequests()

  function refresh() {
    setVersion(version + 1)
  }

  return (
    <div className="cabinet-page">
      <SiteHeader />
      <main className="cabinet">
        <nav className="cabinet__tabs">
          {sections.map((section) => (
            <NavLink
              key={section.id}
              to={`/admin/${section.id}`}
              className={({ isActive }) =>
                isActive ? "cabinet__tab cabinet__tab--active" : "cabinet__tab"
              }
            >
              {section.label}
            </NavLink>
          ))}
        </nav>
        {current.id === "shelters" ? (
          <section className="cabinet__panel">
            {shelters.length === 0 ? (
              <p className="pets__empty">Нет приютов</p>
            ) : (
              <ul className="admin__list">
                {shelters.map((shelter) => (
                  <li key={shelter.id} className="admin__shelter">
                    <span>{shelter.name}</span>
                    <span>{shelter.phone}</span>
                    <span>{shelter.email}</span>
                    <span>{shelter.district} район</span>
                    <span>{shelter.address}</span>
                    <span>{shelter.availableCount} доступно</span>
                  </li>
                ))}
              </ul>
            )}
          </section>
        ) : (
          <section className="cabinet__panel">
            {requests.length === 0 ? (
              <p className="pets__empty">Нет заявок</p>
            ) : (
              <ul className="admin__list">
                {requests.map((request) => (
                  <li key={request.id} className="admin__request">
                    <span>{request.name}</span>
                    <span>{request.address}</span>
                    <span>{request.district} район</span>
                    <span>{request.contactName}</span>
                    <span>{request.phone}</span>
                    <span>{request.email}</span>
                    <span className="admin__actions">
                      <button
                        type="button"
                        className="admin__approve"
                        onClick={() => {
                          approveRegistration(request.id)
                          refresh()
                        }}
                      >
                        Подтвердить
                      </button>
                      <button
                        type="button"
                        className="admin__reject"
                        onClick={() => {
                          rejectRegistration(request.id)
                          refresh()
                        }}
                      >
                        Отказать
                      </button>
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </section>
        )}
      </main>
    </div>
  )
}

export default AdminPage
