<script setup>
import { ref } from 'vue'
import DocumentList from './components/DocumentList.vue'
import PatientSearch from './components/PatientSearch.vue'

const selectedPatient = ref(null)
const searchExpanded = ref(true)

function handlePatientSelect(patient) {
  selectedPatient.value = patient
  searchExpanded.value = false
}

function handleClearPatient() {
  selectedPatient.value = null
  searchExpanded.value = true
}
</script>

<template>
  <div class="app-shell">
    <header class="page-header">
      <div class="header-brand">
        <div class="brand-icon">+</div>
        <div>
          <h1>FHIR Client</h1>
          <p>Search patients and manage clinical documents</p>
        </div>
      </div>
    </header>

    <main class="page-layout" :class="{ 'has-selection': selectedPatient }">
      <PatientSearch
        :selected-patient="selectedPatient"
        :expanded="searchExpanded"
        @select="handlePatientSelect"
        @expand="searchExpanded = true"
        @collapse="searchExpanded = false"
        @clear="handleClearPatient"
      />
      <DocumentList :patient="selectedPatient" />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.08), transparent 28%),
    #f3f6fb;
}

.page-header {
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  padding: 20px 24px;
}

.header-brand {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  font-weight: 700;
}

.page-header h1 {
  margin: 0 0 4px;
  font-size: 1.5rem;
  color: #0f172a;
}

.page-header p {
  margin: 0;
  color: #64748b;
  font-size: 0.92rem;
}

.page-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-layout.has-selection {
  gap: 16px;
}
</style>
