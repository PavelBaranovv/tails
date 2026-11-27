import { useState, type FormEvent } from "react"
import { Link } from "react-router-dom"
import SiteHeader from "./SiteHeader.tsx"
import "./LoginPage.css"

function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [notice, setNotice] = useState("")

  function submit(event: FormEvent) {
    event.preventDefault()
    setNotice("Вход пока не подключён к серверу.")
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
            {notice && <p className="login__notice">{notice}</p>}
            <button type="submit">Войти</button>
          </form>
        </section>
      </main>
    </div>
  )
}

export default LoginPage
