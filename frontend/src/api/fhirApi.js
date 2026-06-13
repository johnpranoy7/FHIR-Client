import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || (import.meta.env.PROD ? '' : '/api')
})

export async function searchPatients(name, family) {
  const params = {}
  if (name) params.name = name
  if (family) params.family = family

  const response = await api.get('/Patient', { params })
  return response.data
}

export async function getDocumentsByPatient(patientId) {
  const response = await api.get('/DocumentReference', {
    params: { patient: patientId }
  })
  return response.data
}

export async function downloadBinary(binaryId) {
  const response = await api.get(`/Binary/${binaryId}`, {
    responseType: 'blob'
  })
  return response
}
