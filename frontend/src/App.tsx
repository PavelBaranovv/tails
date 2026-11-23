import { BrowserRouter, Route, Routes } from "react-router-dom"
import AnimalPage from "./pages/AnimalPage.tsx"
import ApplicationPage from "./pages/ApplicationPage.tsx"
import CatalogPage from "./pages/CatalogPage.tsx"
import LoginPage from "./pages/LoginPage.tsx"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<CatalogPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/animals/:id" element={<AnimalPage />} />
        <Route path="/animals/:id/apply" element={<ApplicationPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
