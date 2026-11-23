import { useState } from "react"
import { Link } from "react-router-dom"
import {
  AGE_OPTIONS,
  DISTRICTS,
  SPECIES,
  ageInMonths,
  formatAge,
  getAnimals,
  sexLabel,
  speciesLabel,
  type Species,
} from "../api/animals"
import "./CatalogPage.css"

function CatalogPage() {
  const [district, setDistrict] = useState("")
  const [species, setSpecies] = useState<Species | "">("")
  const [ageIndex, setAgeIndex] = useState(0)
  const age = AGE_OPTIONS[ageIndex]
  const animals = getAnimals({
    district,
    species,
    minAge: age.minAge,
    maxAge: age.maxAge,
  })

  return (
    <main className="catalog">
      <header className="catalog__header">
        <h1>Хвостики</h1>
      </header>
      <form className="catalog__filters" onSubmit={(event) => event.preventDefault()}>
        <label>
          Район
          <select value={district} onChange={(event) => setDistrict(event.target.value)}>
            <option value="">Любой</option>
            {DISTRICTS.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
        </label>
        <label>
          Вид
          <select
            value={species}
            onChange={(event) => setSpecies(event.target.value as Species | "")}
          >
            <option value="">Любой</option>
            {SPECIES.map((item) => (
              <option key={item.value} value={item.value}>
                {item.label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Возраст
          <select
            value={ageIndex}
            onChange={(event) => setAgeIndex(Number(event.target.value))}
          >
            {AGE_OPTIONS.map((item, index) => (
              <option key={item.label} value={index}>
                {item.label}
              </option>
            ))}
          </select>
        </label>
      </form>
      {animals.length === 0 ? (
        <p className="catalog__empty">Никого не нашли. Измените фильтры.</p>
      ) : (
        <ul className="catalog__grid">
          {animals.map((animal) => {
            const ageMonths = ageInMonths(animal)
            return (
              <li key={animal.id}>
                <Link className="card" to={`/animals/${animal.id}`}>
                  <img src={animal.photoUrl} alt={animal.name} />
                  <div className="card__body">
                    <h2>{animal.name}</h2>
                    <p>{speciesLabel(animal.species)}</p>
                    {animal.sex && <p>{sexLabel(animal.sex)}</p>}
                    {ageMonths != null && <p>{formatAge(ageMonths)}</p>}
                    <p>{animal.district}</p>
                  </div>
                </Link>
              </li>
            )
          })}
        </ul>
      )}
    </main>
  )
}

export default CatalogPage
