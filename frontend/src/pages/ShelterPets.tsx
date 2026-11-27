import { useEffect, useRef, useState } from "react"
import {
  createPet,
  deletePet,
  getPet,
  listShelterPets,
  markPetAdopted,
  PET_STATUSES,
  petBreedLabel,
  petSexLabel,
  petSpeciesLabel,
  petStatusLabel,
  SPECIES,
  updatePet,
  type PetStatus,
} from "../api/pets"
import type { Species } from "../api/animals"
import FilterMenu from "./FilterMenu.tsx"
import ShelterPetForm from "./ShelterPetForm.tsx"

function ShelterPets() {
  const [status, setStatus] = useState<PetStatus | "ALL">("AVAILABLE")
  const [species, setSpecies] = useState<Species | "">("")
  const [openFilter, setOpenFilter] = useState<"status" | "species" | null>(null)
  const [editor, setEditor] = useState<number | "new" | null>(null)
  const [version, setVersion] = useState(0)
  const filtersRef = useRef<HTMLDivElement>(null)
  const pets = listShelterPets(status, species)

  useEffect(() => {
    function close(event: PointerEvent) {
      if (!filtersRef.current?.contains(event.target as Node)) {
        setOpenFilter(null)
      }
    }
    document.addEventListener("pointerdown", close)
    return () => document.removeEventListener("pointerdown", close)
  }, [])

  function adopt(id: number) {
    markPetAdopted(id)
    setVersion(version + 1)
  }

  function closeEditor() {
    setEditor(null)
  }

  const editing = typeof editor === "number" ? getPet(editor) : null
  if (editor !== null && (editor === "new" || editing)) {
    return (
      <section className="cabinet__panel">
        <ShelterPetForm
          key={editor}
          pet={editing}
          onCancel={closeEditor}
          onDelete={() => {
            if (editing) {
              deletePet(editing.id)
            }
            setVersion(version + 1)
            closeEditor()
          }}
          onSave={(input) => {
            if (editing) {
              updatePet(editing.id, input)
            } else {
              createPet(input)
            }
            setVersion(version + 1)
            closeEditor()
          }}
        />
      </section>
    )
  }

  return (
    <div className="pets">
      <div className="pets__toolbar">
        <div className="pets__filters" ref={filtersRef}>
          <FilterMenu
            label="Статус"
            value={status}
            tone="dark"
            open={openFilter === "status"}
            onToggle={() => setOpenFilter((current) => (current === "status" ? null : "status"))}
            onChange={(value) => {
              setStatus(value as PetStatus | "ALL")
              setOpenFilter(null)
            }}
            options={[
              ...PET_STATUSES.map((item) => ({ value: item.value, label: item.label })),
              { value: "ALL", label: "Все" },
            ]}
          />
          <FilterMenu
            label="Вид"
            value={species}
            tone="dark"
            open={openFilter === "species"}
            onToggle={() => setOpenFilter((current) => (current === "species" ? null : "species"))}
            onChange={(value) => {
              setSpecies(value as Species | "")
              setOpenFilter(null)
            }}
            options={[
              { value: "", label: "Все виды" },
              ...SPECIES.map((item) => ({ value: item.value, label: item.label })),
            ]}
          />
        </div>
        <button type="button" className="pets__add" onClick={() => setEditor("new")}>
          Добавить нового питомца
        </button>
      </div>
      <section className="cabinet__panel">
        {pets.length === 0 ? (
          <p className="pets__empty">Нет питомцев</p>
        ) : (
          <ul className="pets__list">
            {pets.map((pet) => (
              <li key={pet.id} className="pets__row">
                {pet.photoUrls[0] ? (
                  <img src={pet.photoUrls[0]} alt="" />
                ) : (
                  <span className="pets__photo" />
                )}
                <div className="pets__info">
                  <h2>{pet.name}</h2>
                  <div className="pets__facts">
                    <span>{petSpeciesLabel(pet)}</span>
                    <span>{petSexLabel(pet.sex)}</span>
                    <span>{pet.age}</span>
                    <span>{petBreedLabel(pet.breed)}</span>
                    <span>{petStatusLabel(pet.status, pet.sex)}</span>
                  </div>
                </div>
                <div className="pets__actions">
                  <button type="button" onClick={() => adopt(pet.id)}>
                    Приютили
                  </button>
                  <button type="button" onClick={() => setEditor(pet.id)}>
                    Редактировать
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

export default ShelterPets
