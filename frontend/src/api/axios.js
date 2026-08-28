import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api/v1',
})

api.interceptors.request.use(config => {
  // Token "Beni hatirla" secimine gore sessionStorage veya localStorage'da olabilir
  const token = sessionStorage.getItem('token') ?? localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default api
