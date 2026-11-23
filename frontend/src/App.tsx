import { BrowserRouter, Route, Routes } from "react-router-dom"
import AnimalPage from "./pages/AnimalPage.tsx"
import CatalogPage from "./pages/CatalogPage.tsx"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<CatalogPage />} />
        <Route path="/animals/:id" element={<AnimalPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
