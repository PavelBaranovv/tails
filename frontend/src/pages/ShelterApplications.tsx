import { useEffect, useRef, useState } from "react"
import { Link } from "react-router-dom"
import {
  APPLICATION_STATUSES,
  applicationStatusLabel,
  listShelterApplications,
  updateApplicationStatus,
  type ApplicationStatus,
} from "../api/applications"
import FilterMenu from "./FilterMenu.tsx"

function ShelterApplications() {
  const [status, setStatus] = useState<ApplicationStatus | "ALL">("ALL")
  const [openFilter, setOpenFilter] = useState(false)
  const [changingId, setChangingId] = useState<number | null>(null)
  const [version, setVersion] = useState(0)
  const filtersRef = useRef<HTMLDivElement>(null)
  const applications = listShelterApplications(status)

  useEffect(() => {
    function close(event: PointerEvent) {
      const target = event.target
      if (!filtersRef.current?.contains(target as Node)) {
        setOpenFilter(false)
      }
      if (!(target instanceof Element) || !target.closest(".apps__change")) {
        setChangingId(null)
      }
    }
    document.addEventListener("pointerdown", close)
    return () => document.removeEventListener("pointerdown", close)
  }, [])

  function chooseStatus(next: ApplicationStatus) {
    if (changingId != null) {
      updateApplicationStatus(changingId, next)
      setVersion(version + 1)
    }
    setChangingId(null)
  }

  return (
    <div className="apps">
      <div className="pets__toolbar">
        <div className="pets__filters" ref={filtersRef}>
          <FilterMenu
            label="Статус"
            value={status}
            tone="dark"
            open={openFilter}
            onToggle={() => setOpenFilter((current) => !current)}
            onChange={(value) => {
              setStatus(value as ApplicationStatus | "ALL")
              setOpenFilter(false)
            }}
            options={[
              ...APPLICATION_STATUSES.map((item) => ({ value: item.value, label: item.label })),
              { value: "ALL", label: "Все" },
            ]}
          />
        </div>
      </div>
      <section className="cabinet__panel">
        {applications.length === 0 ? (
          <p className="pets__empty">Нет заявок</p>
        ) : (
          <ul className="apps__list">
            {applications.map((application) => (
              <li key={application.id} className="apps__row">
                <span className="apps__id">№{application.id}</span>
                <span className={`apps__status apps__status--${application.status}`}>
                  {applicationStatusLabel(application.status)}
                </span>
                <span>{application.applicantName}</span>
                <span>{application.applicantPhone}</span>
                <Link
                  to={`/animals/${application.animalId}`}
                  state={{ backTo: "/shelter/applications" }}
                >
                  {application.animalName}
                </Link>
                <div className="apps__change">
                  <button
                    type="button"
                    aria-expanded={changingId === application.id}
                    onClick={() =>
                      setChangingId((current) => (current === application.id ? null : application.id))
                    }
                  >
                    Сменить статус
                  </button>
                  {changingId === application.id && (
                    <ul>
                      {APPLICATION_STATUSES.map((item) => (
                        <li key={item.value}>
                          <button
                            type="button"
                            className={application.status === item.value ? "is-current" : undefined}
                            onClick={() => chooseStatus(item.value)}
                          >
                            {item.label}
                          </button>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

export default ShelterApplications
