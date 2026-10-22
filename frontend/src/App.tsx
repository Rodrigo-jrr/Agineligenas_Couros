import { CadeiaFornecimento } from './components/CadeiaFornecimento'
import { Catalogo } from './components/catalogo/Catalogo'
import { ComercialInfo } from './components/ComercialInfo'
import { Contato } from './components/Contato'
import { Footer } from './components/Footer'
import { Fornecedores } from './components/Fornecedores'
import { Header } from './components/Header'
import { Hero } from './components/Hero'

function App() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="flex-1">
        <Hero />
        <Catalogo />
        <ComercialInfo />
        <Fornecedores />
        <CadeiaFornecimento />
        <Contato />
      </main>
      <Footer />
    </div>
  )
}

export default App
