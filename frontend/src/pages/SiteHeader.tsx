import { useEffect, useState } from "react"
import { Link, useLocation, useNavigate } from "react-router-dom"
import { getShelterSession, logoutShelter, type ShelterSession } from "../api/auth"
import "./SiteHeader.css"

function SiteHeader({ onBrandClick }: { onBrandClick?: () => void }) {
  const navigate = useNavigate()
  const location = useLocation()
  const [session, setSession] = useState<ShelterSession | null>(getShelterSession)
  const accountLink =
    session?.role === "ADMIN"
      ? "/admin/shelters"
      : location.pathname.startsWith("/shelter")
        ? "/shelter/profile"
        : "/shelter/pets"

  useEffect(() => {
    function refresh() {
      setSession(getShelterSession())
    }
    window.addEventListener("tails-shelter-session", refresh)
    return () => window.removeEventListener("tails-shelter-session", refresh)
  }, [])

  function logout() {
    logoutShelter()
    setSession(null)
    navigate("/")
  }

  return (
    <header className="site-header">
      <div className="site-header__inner">
        <Link className="site-header__brand" to="/" onClick={onBrandClick}>
          <img src="/logo.png" alt="" />
          <h1>Хвостики</h1>
        </Link>
        {session ? (
          <div className="site-header__account">
            <Link className="site-header__shelter" to={accountLink}>
              {session.name}
            </Link>
            <button type="button" className="site-header__login" onClick={logout}>
              Выйти
            </button>
          </div>
        ) : (
          <Link className="site-header__login" to="/login">
            Вход для приюта
          </Link>
        )}
      </div>
    </header>
  )
}

export default SiteHeader
