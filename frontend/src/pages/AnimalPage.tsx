import { Link, useParams } from "react-router-dom"
import { ageInMonths, formatAge, getAnimal, sexLabel, speciesLabel } from "../api/animals"
import "./AnimalPage.css"

function AnimalPage() {
  const params = useParams()
  const animal = getAnimal(Number(params.id))

  if (!animal) {
    return (
      <main className="profile">
        <Link to="/">Назад</Link>
        <p>Животное не найдено.</p>
      </main>
    )
  }

  const ageMonths = ageInMonths(animal)

  return (
    <main className="profile">
      <Link to="/">Назад</Link>
      <img src={animal.photoUrl} alt={animal.name} />
      <h1>{animal.name}</h1>
      <p>{speciesLabel(animal.species)}</p>
      <p>{ageMonths == null ? "Возраст неизвестен" : formatAge(ageMonths)}</p>
      <p>{animal.sex == null ? "Пол неизвестен" : sexLabel(animal.sex)}</p>
      <p>{animal.breed == null ? "Порода неизвестна" : animal.breed}</p>
      {animal.description && <p>{animal.description}</p>}
      <Link className="profile__adopt" to={`/animals/${animal.id}/apply`}>
        Приютить
      </Link>
      <section>
        <h2>{animal.shelterName}</h2>
        <p>{animal.phone}</p>
        <p>{animal.district}</p>
        <p>{animal.address}</p>
      </section>
    </main>
  )
}

export default AnimalPage
