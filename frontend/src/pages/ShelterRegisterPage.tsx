import { useState, type FormEvent, type ReactNode } from "react"
import { Link } from "react-router-dom"
import { DISTRICTS } from "../api/animals"
import { createShelterRegistration, type ShelterRegistration } from "../api/registrations"
import SiteHeader from "./SiteHeader.tsx"
import "./ShelterRegisterPage.css"

const NAME = /^[\p{L}\p{N} «»"'—\-.!, :/\\@|#()*%$^]+$/u
const ADDRESS = /^[\p{L}\p{N} ,.\-/№]+$/u
const PERSON = /^[\p{L} -]+$/u
const PASSWORD = /^[A-Za-z0-9!"#$%&()*+,\-./:;<=>?@[\\\]^_]+$/

function ShelterRegisterPage() {
  const [name, setName] = useState("")
  const [district, setDistrict] = useState("")
  const [address, setAddress] = useState("")
  const [contactName, setContactName] = useState("")
  const [phone, setPhone] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [error, setError] = useState("")
  const [created, setCreated] = useState<ShelterRegistration | null>(null)

  function submit(event: FormEvent) {
    event.preventDefault()
    const shelterName = name.trim()
    const shelterAddress = address.trim()
    const person = contactName.trim()
    const shelterPhone = phone.trim().replace(/[\s()-]/g, "")
    const shelterEmail = email.trim()

    if (shelterName.length < 2 || shelterName.length > 256 || !NAME.test(shelterName)) {
      setError("Название: от 2 до 256 символов, буквы, цифры и знаки препинания.")
      return
    }
    if (!district) {
      setError("Выберите район.")
      return
    }
    if (shelterAddress.length < 2 || shelterAddress.length > 256 || !ADDRESS.test(shelterAddress)) {
      setError("Адрес: от 2 до 256 символов, буквы, цифры и знаки , . - / №.")
      return
    }
    if (person.length < 2 || person.length > 128 || !PERSON.test(person)) {
      setError("ФИО: от 2 до 128 символов, только буквы, пробел и дефис.")
      return
    }
    if (!/^[+\d]{7,18}$/.test(shelterPhone)) {
      setError("Телефон: от 7 до 18 символов, только цифры и знак «+».")
      return
    }
    if (
      shelterEmail.length < 5 ||
      shelterEmail.length > 254 ||
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(shelterEmail)
    ) {
      setError("Email: от 5 до 254 символов, со знаком @ и доменом.")
      return
    }
    if (password.length < 8 || password.length > 64 || !PASSWORD.test(password)) {
      setError("Пароль: от 8 до 64 символов, латиница, цифры и специальные знаки.")
      return
    }
    if (password !== confirmPassword) {
      setError("Пароли не совпадают.")
      return
    }

    setError("")
    setCreated(
      createShelterRegistration({
        name: shelterName,
        district,
        address: shelterAddress,
        contactName: person,
        phone: shelterPhone,
        email: shelterEmail,
        password,
      }),
    )
  }

  return (
    <div className="register-page">
      <SiteHeader />
      <main className="register">
        <Link className="register__back" to="/login">
          ← Назад ко входу
        </Link>
        <section className="register__card">
          {created ? (
            <>
              <h1>Заявка отправлена</h1>
              <p>Заявка №{created.id} на регистрацию приюта сохранена. Её проверит администратор.</p>
              <Link className="register__submit" to="/login">
                Вернуться ко входу
              </Link>
            </>
          ) : (
            <>
              <h1>Регистрация приюта</h1>
              <form onSubmit={submit}>
                <Field label="Название приюта">
                  <input value={name} onChange={(event) => setName(event.target.value)} />
                </Field>
                <Field label="Район">
                  <select value={district} onChange={(event) => setDistrict(event.target.value)}>
                    <option value="">Выберите район</option>
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
                <Field label="ФИО">
                  <input value={contactName} onChange={(event) => setContactName(event.target.value)} />
                </Field>
                <Field label="Телефон">
                  <input
                    value={phone}
                    inputMode="tel"
                    autoComplete="tel"
                    placeholder="+7 (___) ___-__-__"
                    onChange={(event) => setPhone(event.target.value)}
                  />
                </Field>
                <Field label="Email">
                  <input
                    type="email"
                    value={email}
                    autoComplete="email"
                    onChange={(event) => setEmail(event.target.value)}
                  />
                </Field>
                <Field label="Пароль">
                  <input
                    type="password"
                    value={password}
                    autoComplete="new-password"
                    onChange={(event) => setPassword(event.target.value)}
                  />
                </Field>
                <Field label="Подтверждение пароля">
                  <input
                    type="password"
                    value={confirmPassword}
                    autoComplete="new-password"
                    onChange={(event) => setConfirmPassword(event.target.value)}
                  />
                </Field>
                {error && <p className="register__error">{error}</p>}
                <button type="submit">Отправить заявку</button>
              </form>
            </>
          )}
        </section>
      </main>
    </div>
  )
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label>
      <span>
        {label} <span className="register__required">*</span>
      </span>
      {children}
    </label>
  )
}

export default ShelterRegisterPage
