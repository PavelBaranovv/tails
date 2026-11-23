import { useState, type FormEvent } from "react"
import { Link, useParams } from "react-router-dom"
import { createApplication, type Application } from "../api/applications"
import { getAnimal } from "../api/animals"
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
      <main className="application">
        <Link to="/">Назад</Link>
        <p>Животное не найдено.</p>
      </main>
    )
  }

  function submit(event: FormEvent) {
    event.preventDefault()
    const applicantName = name.trim()
    const applicantPhone = phone.trim()
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
      <main className="application">
        <Link to={`/animals/${animal.id}`}>Назад к анкете</Link>
        <h1>Заявка создана</h1>
        <p>
          Заявка №{created.id} на усыновление отправлена в приют. {animal.name} уже готовится стать вашим хвостиком.
        </p>
      </main>
    )
  }

  return (
    <main className="application">
      <Link to={`/animals/${animal.id}`}>Назад к анкете</Link>
      <h1>Приютить {animal.name}</h1>
      <form onSubmit={submit}>
        <label>
          Имя
          <input value={name} onChange={(event) => setName(event.target.value)} />
        </label>
        <label>
          Телефон
          <input value={phone} onChange={(event) => setPhone(event.target.value)} />
        </label>
        {error && <p className="application__error">{error}</p>}
        <button type="submit">Отправить</button>
      </form>
    </main>
  )
}

export default ApplicationPage
