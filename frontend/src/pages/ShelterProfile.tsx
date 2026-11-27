import { useState, type FormEvent, type ReactNode } from "react"
import { DISTRICTS } from "../api/animals"
import { updateShelterSessionName } from "../api/auth"
import { getShelterProfile, updateShelterProfile, type ShelterProfile } from "../api/shelterProfile"

const NAME = /^[\p{L}\p{N} «»"'—\-.!, :/\\@|#()*%$^]+$/u
const ADDRESS = /^[\p{L}\p{N} ,.\-/№]+$/u

function ShelterProfilePage() {
  const [profile, setProfile] = useState(getShelterProfile)
  const [editing, setEditing] = useState(false)

  function save(next: ShelterProfile) {
    const saved = updateShelterProfile(next)
    updateShelterSessionName(saved.name)
    setProfile(saved)
    setEditing(false)
  }

  return (
    <section className="cabinet__panel">
      {editing ? (
        <ProfileForm profile={profile} onCancel={() => setEditing(false)} onSave={save} />
      ) : (
        <div className="profile">
          <h1>{profile.name}</h1>
          <dl>
            <div>
              <dt>Район</dt>
              <dd>{profile.district} район</dd>
            </div>
            <div>
              <dt>Адрес</dt>
              <dd>{profile.address}</dd>
            </div>
            <div>
              <dt>Телефон</dt>
              <dd>{profile.phone}</dd>
            </div>
          </dl>
          <button type="button" onClick={() => setEditing(true)}>
            Редактировать
          </button>
        </div>
      )}
    </section>
  )
}

function ProfileForm({
  profile,
  onCancel,
  onSave,
}: {
  profile: ShelterProfile
  onCancel: () => void
  onSave: (next: ShelterProfile) => void
}) {
  const [name, setName] = useState(profile.name)
  const [district, setDistrict] = useState(profile.district)
  const [address, setAddress] = useState(profile.address)
  const [phone, setPhone] = useState(profile.phone)
  const [error, setError] = useState("")

  function submit(event: FormEvent) {
    event.preventDefault()
    const shelterName = name.trim()
    const shelterAddress = address.trim()
    const shelterPhone = phone.trim().replace(/[\s()-]/g, "")

    if (shelterName.length < 2 || shelterName.length > 256 || !NAME.test(shelterName)) {
      setError("Название: от 2 до 256 символов, буквы, цифры и знаки препинания.")
      return
    }
    if (!DISTRICTS.includes(district)) {
      setError("Выберите район.")
      return
    }
    if (shelterAddress.length < 2 || shelterAddress.length > 256 || !ADDRESS.test(shelterAddress)) {
      setError("Адрес: от 2 до 256 символов, буквы, цифры и знаки , . - / №.")
      return
    }
    if (!/^[+\d]{7,18}$/.test(shelterPhone)) {
      setError("Телефон: от 7 до 18 символов, только цифры и знак «+».")
      return
    }

    setError("")
    onSave({
      name: shelterName,
      district,
      address: shelterAddress,
      phone: shelterPhone,
    })
  }

  return (
    <form className="pet-form" onSubmit={submit}>
      <h1>Редактирование профиля</h1>
      <Field label="Название приюта">
        <input value={name} onChange={(event) => setName(event.target.value)} />
      </Field>
      <Field label="Район">
        <select value={district} onChange={(event) => setDistrict(event.target.value)}>
          {DISTRICTS.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </Field>
      <Field label="Адрес">
        <input value={address} onChange={(event) => setAddress(event.target.value)} />
      </Field>
      <Field label="Телефон">
        <input value={phone} onChange={(event) => setPhone(event.target.value)} />
      </Field>
      {error && <p className="pet-form__error">{error}</p>}
      <div className="pet-form__actions">
        <button className="pet-form__save" type="submit">
          Сохранить
        </button>
        <button className="pet-form__cancel" type="button" onClick={onCancel}>
          Отмена
        </button>
      </div>
    </form>
  )
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label>
      <span>
        {label}
        <span className="pet-form__required"> *</span>
      </span>
      {children}
    </label>
  )
}

export default ShelterProfilePage
