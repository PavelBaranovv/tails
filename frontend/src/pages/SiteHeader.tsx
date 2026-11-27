import { Link } from "react-router-dom"
import "./SiteHeader.css"

function SiteHeader({ onBrandClick }: { onBrandClick?: () => void }) {
  return (
    <header className="site-header">
      <div className="site-header__inner">
        <Link className="site-header__brand" to="/" onClick={onBrandClick}>
          <img src="/logo.png" alt="" />
          <h1>Хвостики</h1>
        </Link>
        <Link className="site-header__login" to="/login">
          Вход для приюта
        </Link>
      </div>
    </header>
  )
}

export default SiteHeader
