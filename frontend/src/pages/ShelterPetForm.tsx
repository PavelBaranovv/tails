import { useState, type FormEvent, type ReactNode } from "react"
import {
  formatPetAge,
  MONTHS,
  PET_STATUSES,
  petStatusLabel,
  type PetInput,
  type PetStatus,
  type ShelterPetView,
} from "../api/pets"
import type { Sex, Species } from "../api/animals"

const NAME = /^[\p{L} -]+$/u
const TEXT = /^[\p{L}\p{N}\s!"#$%&()*+,\-./:;<=>?@[\\\]^_]+$/u
const PHOTO_LIMIT = 5 * 1024 * 1024

const ANIMALS: { value: Species; label: string }[] = [
  { value: "CAT", label: "Кошка" },
  { value: "DOG", label: "Собака" },
  { value: "PARROT", label: "Попугай" },
  { value: "OTHER", label: "Другое животное" },
]

function ShelterPetForm({
  pet,
  onCancel,
  onSave,
  onDelete,
}: {
  pet: ShelterPetView | null
  onCancel: () => void
  onSave: (input: PetInput) => void
  onDelete: () => void
}) {
  const [name, setName] = useState(pet?.name ?? "")
  const [species, setSpecies] = useState<Species | "">(pet?.species ?? "")
  const [speciesOther, setSpeciesOther] = useState(pet?.speciesOther ?? "")
  const [sex, setSex] = useState<Sex | "">(pet?.sex ?? "")
  const [birthMonth, setBirthMonth] = useState(pet?.birthMonth == null ? "" : String(pet.birthMonth))
  const [birthYear, setBirthYear] = useState(pet?.birthYear == null ? "" : String(pet.birthYear))
  const [breed, setBreed] = useState(pet?.breed ?? "")
  const [description, setDescription] = useState(pet?.description ?? "")
  const [status, setStatus] = useState<PetStatus>(pet?.status ?? "AVAILABLE")
  const [photoUrls, setPhotoUrls] = useState<string[]>(pet?.photoUrls ?? [])
  const [error, setError] = useState("")

  const month = birthMonth === "" ? null : Number(birthMonth)
  const year = birthYear.trim() === "" ? null : Number(birthYear.trim())
  const age = formatPetAge(month, Number.isInteger(year) ? year : null)

  function submit(event: FormEvent) {
    event.preventDefault()
    const petName = name.trim()
    const other = speciesOther.trim()
    const petBreed = breed.trim()
    const text = description.trim()
    const currentYear = new Date().getFullYear()

    if (petName.length < 2 || petName.length > 64 || !NAME.test(petName)) {
      setError("Имя: от 2 до 64 символов, только буквы, пробел и дефис.")
      return
    }
    if (!species) {
      setError("Выберите животное.")
      return
    }
    if (species === "OTHER" && (other.length < 1 || other.length > 64)) {
      setError("Укажите тип животного, до 64 символов.")
      return
    }
    if ((month == null) !== (year == null) || (year != null && !Number.isInteger(year))) {
      setError("Укажите месяц и год рождения вместе.")
      return
    }
    if (year != null && (year < currentYear - 200 || year > currentYear)) {
      setError(`Год рождения: от ${currentYear - 200} до ${currentYear}.`)
      return
    }
    if (month != null && year != null) {
      const now = new Date()
      const future = year > now.getFullYear() || (year === now.getFullYear() && month > now.getMonth() + 1)
      if (future) {
        setError("Дата рождения не может быть в будущем.")
        return
      }
    }
    if (petBreed && (petBreed.length < 2 || petBreed.length > 64 || !NAME.test(petBreed))) {
      setError("Порода: от 2 до 64 символов, только буквы, пробел и дефис.")
      return
    }
    if (text && (text.length < 8 || text.length > 1024 || !TEXT.test(text))) {
      setError("Описание: от 8 до 1024 символов.")
      return
    }

    setError("")
    onSave({
      name: petName,
      species,
      speciesOther: species === "OTHER" ? other : null,
      sex: sex || null,
      birthMonth: month,
      birthYear: year,
      breed: petBreed || null,
      description: text,
      status: pet ? status : "AVAILABLE",
      photoUrls,
    })
  }

  function choosePhotos(files: FileList | null) {
    const selected = files ? [...files] : []
    if (selected.length === 0) {
      return
    }
    const invalid = selected.some(
      (file) => (file.type !== "image/jpeg" && file.type !== "image/png") || file.size > PHOTO_LIMIT,
    )
    if (invalid) {
      setError("Фото: только JPEG или PNG, размер до 5 МБ.")
      return
    }
    setError("")
    Promise.all(selected.map(readPhoto)).then(setPhotoUrls)
  }

  return (
    <form className="pet-form" onSubmit={submit}>
      <h1>{pet ? "Редактирование питомца" : "Новый питомец"}</h1>
      <Field label="Имя" required>
        <input value={name} onChange={(event) => setName(event.target.value)} />
      </Field>
      <Field label="Животное" required>
        <select value={species} onChange={(event) => setSpecies(event.target.value as Species | "")}>
          <option value="">Выберите животное</option>
          {ANIMALS.map((item) => (
            <option key={item.value} value={item.value}>
              {item.label}
            </option>
          ))}
        </select>
      </Field>
      {species === "OTHER" && (
        <Field label="Тип животного" required>
          <input value={speciesOther} onChange={(event) => setSpeciesOther(event.target.value)} />
        </Field>
      )}
      <Field label="Пол">
        <select value={sex} onChange={(event) => setSex(event.target.value as Sex | "")}>
          <option value="">Не указан</option>
          <option value="MALE">Мальчик</option>
          <option value="FEMALE">Девочка</option>
        </select>
      </Field>
      <div className="pet-form__birth">
        <Field label="Месяц рождения">
          <select value={birthMonth} onChange={(event) => setBirthMonth(event.target.value)}>
            <option value="">Не указан</option>
            {MONTHS.map((item, index) => (
              <option key={item} value={index + 1}>
                {item}
              </option>
            ))}
          </select>
        </Field>
        <Field label="Год рождения">
          <input
            inputMode="numeric"
            value={birthYear}
            onChange={(event) => setBirthYear(event.target.value)}
          />
        </Field>
      </div>
      <p className="pet-form__age">Возраст: {age}</p>
      <Field label="Порода">
        <input value={breed} onChange={(event) => setBreed(event.target.value)} />
      </Field>
      <Field label="Текстовое описание">
        <textarea rows={5} value={description} onChange={(event) => setDescription(event.target.value)} />
      </Field>
      <Field label="Фотографии">
        <input
          type="file"
          accept="image/jpeg,image/png"
          multiple
          onChange={(event) => choosePhotos(event.target.files)}
        />
        <span className="pet-form__hint">JPEG или PNG, до 5 МБ</span>
        {photoUrls.length > 0 && (
          <span className="pet-form__photos">
            {photoUrls.map((url) => (
              <img key={url} src={url} alt="" />
            ))}
          </span>
        )}
      </Field>
      {pet && (
        <Field label="Статус" required>
          <select value={status} onChange={(event) => setStatus(event.target.value as PetStatus)}>
            {PET_STATUSES.map((item) => (
              <option key={item.value} value={item.value}>
                {petStatusLabel(item.value, sex || null)}
              </option>
            ))}
          </select>
        </Field>
      )}
      {error && <p className="pet-form__error">{error}</p>}
      <div className="pet-form__actions">
        <button className="pet-form__save" type="submit">
          Сохранить
        </button>
        <button className="pet-form__cancel" type="button" onClick={onCancel}>
          Отмена
        </button>
        {pet && (
          <button className="pet-form__delete" type="button" onClick={onDelete}>
            Удалить
          </button>
        )}
      </div>
    </form>
  )
}

function Field({ label, required, children }: { label: string; required?: boolean; children: ReactNode }) {
  return (
    <label>
      <span>
        {label}
        {required && <span className="pet-form__required"> *</span>}
      </span>
      {children}
    </label>
  )
}

function readPhoto(file: File) {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(String(reader.result))
    reader.onerror = () => reject(reader.error)
    reader.readAsDataURL(file)
  })
}

export default ShelterPetForm
