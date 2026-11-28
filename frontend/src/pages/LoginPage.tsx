import { useState, type FormEvent } from "react"
import { Link, useNavigate } from "react-router-dom"
import { loginShelter } from "../api/auth"
import SiteHeader from "./SiteHeader.tsx"
import "./LoginPage.css"

function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const navigate = useNavigate()

  function submit(event: FormEvent) {
    event.preventDefault()
    const role = loginShelter(email, password)
    if (!role) {
      setError("Неверный email или пароль")
      return
    }
    setError("")
    navigate(role === "ADMIN" ? "/admin/shelters" : "/shelter/pets")
  }

  return (
    <div className="login-page">
      <SiteHeader />
      <main className="login">
        <Link className="login__back" to="/">
          ← Назад к каталогу
        </Link>
        <section className="login__card">
          <h1>Вход для приюта</h1>
          <form onSubmit={submit}>
            <label>
              Email
              <input
                type="email"
                value={email}
                placeholder="Введите email"
                autoComplete="email"
                onChange={(event) => setEmail(event.target.value)}
              />
            </label>
            <label>
              Пароль
              <input
                type="password"
                value={password}
                placeholder="Введите пароль"
                autoComplete="current-password"
                onChange={(event) => setPassword(event.target.value)}
              />
            </label>
            {error && <p className="login__error">{error}</p>}
            <button type="submit">Войти</button>
          </form>
          <p className="login__register">
            Приют ещё не зарегистрирован? <Link to="/register">Подать заявку</Link>
          </p>
        </section>
      </main>
    </div>
  )
}

export default LoginPage
