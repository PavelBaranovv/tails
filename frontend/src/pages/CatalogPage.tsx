import { useEffect, useRef, useState } from "react"
import { Link } from "react-router-dom"
import SiteHeader from "./SiteHeader.tsx"
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

type Option = { value: string; label: string }

function FilterMenu({
  label,
  value,
  options,
  tone,
  open,
  onToggle,
  onChange,
}: {
  label: string
  value: string
  options: Option[]
  tone: "dark" | "warm"
  open: boolean
  onToggle: () => void
  onChange: (value: string) => void
}) {
  const selected = options.find((option) => option.value === value)
  const active = selected != null && selected.value !== ""
  const caption = active ? selected.label : label

  return (
    <div className={`filter filter--${tone}${active ? " filter--active" : ""}`}>
      <button type="button" aria-expanded={open} onClick={onToggle}>
        {caption}
        <span aria-hidden="true">{open ? "▴" : "▾"}</span>
      </button>
      {open && (
        <ul>
          {options.map((option) => (
            <li key={option.label}>
              <button
                type="button"
                className={option.value === value ? "is-selected" : undefined}
                onClick={() => onChange(option.value)}
              >
                {option.label}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

function CatalogPage() {
  const [district, setDistrict] = useState("")
  const [species, setSpecies] = useState<Species | "">("")
  const [ageIndex, setAgeIndex] = useState(0)
  const [openFilter, setOpenFilter] = useState<"district" | "species" | "age" | null>(null)
  const filtersRef = useRef<HTMLDivElement>(null)
  const age = AGE_OPTIONS[ageIndex]
  const animals = getAnimals({
    district,
    species,
    minAge: age.minAge,
    maxAge: age.maxAge,
  })

  useEffect(() => {
    function close(event: PointerEvent) {
      if (!filtersRef.current?.contains(event.target as Node)) {
        setOpenFilter(null)
      }
    }
    document.addEventListener("pointerdown", close)
    return () => document.removeEventListener("pointerdown", close)
  }, [])

  function toggle(name: "district" | "species" | "age") {
    setOpenFilter((current) => (current === name ? null : name))
  }

  return (
    <div className="catalog-page">
      <SiteHeader
        onBrandClick={() => {
          setDistrict("")
          setSpecies("")
          setAgeIndex(0)
          setOpenFilter(null)
        }}
      />
      <main className="catalog">
      <h2 className="catalog__title">Питомцы</h2>
      <div className="catalog__filters" ref={filtersRef}>
        <FilterMenu
          label="Район"
          value={district}
          tone="dark"
          open={openFilter === "district"}
          onToggle={() => toggle("district")}
          onChange={(value) => {
            setDistrict(value)
            setOpenFilter(null)
          }}
          options={[
            { value: "", label: "Любой" },
            ...DISTRICTS.map((item) => ({ value: item, label: item })),
          ]}
        />
        <FilterMenu
          label="Вид"
          value={species}
          tone="warm"
          open={openFilter === "species"}
          onToggle={() => toggle("species")}
          onChange={(value) => {
            setSpecies(value as Species | "")
            setOpenFilter(null)
          }}
          options={[
            { value: "", label: "Любой" },
            ...SPECIES.map((item) => ({ value: item.value, label: item.label })),
          ]}
        />
        <FilterMenu
          label="Возраст"
          value={ageIndex === 0 ? "" : String(ageIndex)}
          tone="warm"
          open={openFilter === "age"}
          onToggle={() => toggle("age")}
          onChange={(value) => {
            setAgeIndex(value === "" ? 0 : Number(value))
            setOpenFilter(null)
          }}
          options={AGE_OPTIONS.map((item, index) => ({
            value: index === 0 ? "" : String(index),
            label: item.label,
          }))}
        />
      </div>
      {animals.length === 0 ? (
        <p className="catalog__empty">Никого не нашли. Попробуйте изменить фильтры.</p>
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
                    <p>{speciesLabel(animal.species, animal.sex)}</p>
                    {animal.sex && <p>{sexLabel(animal.sex)}</p>}
                    {ageMonths != null && <p>{formatAge(ageMonths)}</p>}
                    <p>{animal.district} район</p>
                  </div>
                </Link>
              </li>
            )
          })}
        </ul>
      )}
      </main>
    </div>
  )
}

export default CatalogPage
