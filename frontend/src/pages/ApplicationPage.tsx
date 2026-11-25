import { useState, type FormEvent } from "react"
import { Link, useParams } from "react-router-dom"
import { createApplication, type Application } from "../api/applications"
import {
  ageInMonths,
  formatAge,
  getAnimal,
  sexLabel,
  speciesLabel,
  type AnimalCard,
} from "../api/animals"
import SiteHeader from "./SiteHeader.tsx"
import "./ApplicationPage.css"

function ApplicationPage() {
  const params = useParams()
  const animal = getAnimal(Number(params.id))
  const [name, setName] = useState("")
  const [phone, setPhone] = useState("")
  const [error, setError] = useState("")
  const [created, setCreated] = useState<Application | null>(null)

  if (!animal) {
    return (
      <div className="application-page">
        <SiteHeader />
        <main className="application">
          <p>Животное не найдено.</p>
          <Link to="/">Назад к каталогу</Link>
        </main>
      </div>
    )
  }

  function submit(event: FormEvent) {
    event.preventDefault()
    const applicantName = name.trim()
    const applicantPhone = phone.trim().replace(/[\s()-]/g, "")
    if (applicantName.length < 2 || applicantName.length > 128) {
      setError("Имя должно быть от 2 до 128 символов.")
      return
    }
    if (!/^[+\d]{7,18}$/.test(applicantPhone)) {
      setError("Телефон: от 7 до 18 символов, только цифры и знак «+».")
      return
    }
    setError("")
    setCreated(
      createApplication({
        animalId: animal!.id,
        applicantName,
        applicantPhone,
      }),
    )
  }

  if (created) {
    return (
      <div className="application-page">
        <SiteHeader />
        <main className="application">
          <section className="application__card">
            <h1>Заявка создана!</h1>
            <div className="application__success">
              <p>Заявка №{created.id} на усыновление отправлена в приют.</p>
              <p>{animal.name} уже готовится стать вашим хвостиком.</p>
            </div>
            <Link className="application__home" to="/">
              Вернуться на главную
            </Link>
          </section>
        </main>
      </div>
    )
  }

  const ageMonths = ageInMonths(animal)

  return (
    <div className="application-page">
      <SiteHeader />
      <main className="application">
        <Link className="application__back" to={`/animals/${animal.id}`}>
          ← Назад к профилю животного
        </Link>
        <section className="application__card">
          <h1>Заполните заявку</h1>
          <p className="application__lead">Мы передадим её в приют</p>
          <AnimalSummary animal={animal} ageMonths={ageMonths} />
          <form onSubmit={submit}>
            <label>
              <span>
                Имя <span className="application__required">*</span>
              </span>
              <input
                value={name}
                placeholder="Введите ваше имя"
                autoComplete="name"
                onChange={(event) => setName(event.target.value)}
              />
            </label>
            <label>
              <span>
                Телефон <span className="application__required">*</span>
              </span>
              <input
                value={phone}
                placeholder="+7 (___) ___-__-__"
                autoComplete="tel"
                inputMode="tel"
                onChange={(event) => setPhone(event.target.value)}
              />
            </label>
            {error && <p className="application__error">{error}</p>}
            <div className="application__actions">
              <button type="submit">Отправить заявку</button>
              <Link to={`/animals/${animal.id}`}>Отмена</Link>
            </div>
          </form>
        </section>
      </main>
    </div>
  )
}

function AnimalSummary({ animal, ageMonths }: { animal: AnimalCard; ageMonths: number | null }) {
  return (
    <div className="application__animal">
      <img src={animal.photoUrl} alt={animal.name} />
      <div>
        <h2>{animal.name}</h2>
        <ul>
          <li>{speciesLabel(animal.species, animal.sex)}</li>
          <li>{animal.sex == null ? "Пол неизвестен" : sexLabel(animal.sex)}</li>
          <li>{ageMonths == null ? "Возраст неизвестен" : formatAge(ageMonths)}</li>
          <li>{animal.breed == null ? "Порода неизвестна" : animal.breed}</li>
        </ul>
      </div>
    </div>
  )
}

export default ApplicationPage
