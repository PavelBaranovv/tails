import { Link, useParams } from "react-router-dom"
import { ageInMonths, formatAge, getAnimal, sexLabel, speciesLabel } from "../api/animals"
import SiteHeader from "./SiteHeader.tsx"
import "./AnimalPage.css"

function AnimalPage() {
  const params = useParams()
  const animal = getAnimal(Number(params.id))

  if (!animal) {
    return (
      <div className="catalog-page">
        <SiteHeader />
        <main className="profile">
          <p>Животное не найдено.</p>
          <Link to="/">Назад к каталогу</Link>
        </main>
      </div>
    )
  }

  const ageMonths = ageInMonths(animal)

  return (
    <div className="catalog-page">
      <SiteHeader />
      <main className="profile">
        <Link className="profile__back" to="/">
          ← Назад к каталогу
        </Link>
        <div className="profile__top">
          <div className="profile__gallery">
            <img className="profile__photo" src={animal.photoUrl} alt={animal.name} />
            <div className="profile__thumbs">
              <img src={animal.photoUrl} alt="" />
            </div>
          </div>
          <section className="profile__card">
            <h1>{animal.name}</h1>
            <dl>
              <div>
                <dt>Вид животного</dt>
                <dd>{speciesLabel(animal.species, animal.sex)}</dd>
              </div>
              <div>
                <dt>Пол</dt>
                <dd>{animal.sex == null ? "Пол неизвестен" : sexLabel(animal.sex)}</dd>
              </div>
              <div>
                <dt>Возраст</dt>
                <dd>{ageMonths == null ? "Возраст неизвестен" : formatAge(ageMonths)}</dd>
              </div>
              <div>
                <dt>Порода</dt>
                <dd>{animal.breed == null ? "Порода неизвестна" : animal.breed}</dd>
              </div>
            </dl>
            {animal.description && (
              <div className="profile__description">
                <h2>Описание</h2>
                <p>{animal.description}</p>
              </div>
            )}
            <Link className="profile__adopt" to={`/animals/${animal.id}/apply`}>
              Приютить
            </Link>
          </section>
        </div>
        <section className="profile__shelter">
          <h2>О приюте</h2>
          <div className="profile__shelter-grid">
            <div>
              <span>Название приюта</span>
              <p>{animal.shelterName}</p>
            </div>
            <div>
              <span>Район</span>
              <p>{animal.district} район</p>
            </div>
            <div>
              <span>Контактные данные</span>
              <p>{animal.phone}</p>
            </div>
            <div>
              <span>Адрес</span>
              <p>
                {animal.address}
                <br />
                Санкт-Петербург
              </p>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AnimalPage
