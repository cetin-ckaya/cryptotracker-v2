import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

// "Beni hatirla" isaretliyse localStorage (tarayici kapansa da kalir),
// degilse sessionStorage (sekme kapaninca silinir) kullanilir.
function read(key) {
  return sessionStorage.getItem(key) ?? localStorage.getItem(key)
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => read('token'))
  const [user, setUser] = useState(() => {
    const u = read('user')
    return u ? JSON.parse(u) : null
  })

  function login(token, userData, remember = true) {
    const store = remember ? localStorage : sessionStorage
    // Once her iki depoyu da temizle ki eski oturumdan artik kalmasin
    logoutStorage()
    store.setItem('token', token)
    store.setItem('user', JSON.stringify(userData))
    setToken(token)
    setUser(userData)
  }

  function logoutStorage() {
    for (const store of [localStorage, sessionStorage]) {
      store.removeItem('token')
      store.removeItem('user')
    }
  }

  function logout() {
    logoutStorage()
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
