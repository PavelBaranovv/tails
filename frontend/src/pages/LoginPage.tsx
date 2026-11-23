import { useState, type FormEvent } from "react"
import { Link } from "react-router-dom"
import "./LoginPage.css"

function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [notice, setNotice] = useState("")

  function submit(event: FormEvent) {
    event.preventDefault()
    setNotice("test login")
  }

  return (
    <main className="login">
      <Link to="/">Назад</Link>
      <h1>Вход для приюта</h1>
      <form onSubmit={submit}>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </label>
        <label>
          Пароль
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </label>
        {notice && <p>{notice}</p>}
        <button type="submit">Войти</button>
      </form>
    </main>
  )
}

export default LoginPage
