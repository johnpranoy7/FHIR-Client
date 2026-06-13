<script setup>
import { ref } from 'vue'
import DocumentList from './components/DocumentList.vue'
import PatientSearch from './components/PatientSearch.vue'

const selectedPatient = ref(null)

function handlePatientSelect(patient) {
  selectedPatient.value = patient
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>FHIR Client</h1>
      <p>Search patients and download available clinical documents.</p>
    </header>

    <PatientSearch @select="handlePatientSelect" />
    <DocumentList :patient="selectedPatient" />
  </div>
</template>
