<script setup>
import { ref } from 'vue'
import { searchPatients } from '../api/fhirApi'

const emit = defineEmits(['select'])

const givenName = ref('')
const familyName = ref('')
const patients = ref([])
const loading = ref(false)
const error = ref('')

async function handleSearch() {
  if (!givenName.value && !familyName.value) {
    error.value = 'Enter a given name, family name, or both.'
    return
  }

  loading.value = true
  error.value = ''

  try {
    patients.value = await searchPatients(givenName.value, familyName.value)
    if (patients.value.length === 0) {
      error.value = 'No patients found.'
    }
  } catch (err) {
    error.value = 'Failed to search patients. Is the backend running on port 8081?'
    patients.value = []
  } finally {
    loading.value = false
  }
}

function selectPatient(patient) {
  emit('select', patient)
}
</script>

<template>
  <div class="card">
    <h2>Patient Search</h2>

    <el-form inline @submit.prevent="handleSearch">
      <el-form-item label="Given Name">
        <el-input v-model="givenName" placeholder="John" clearable />
      </el-form-item>
      <el-form-item label="Family Name">
        <el-input v-model="familyName" placeholder="Smith" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSearch">
          Search
        </el-button>
      </el-form-item>
    </el-form>

    <el-alert v-if="error" :title="error" type="warning" show-icon :closable="false" />

    <el-table
      v-if="patients.length"
      :data="patients"
      stripe
      style="width: 100%; margin-top: 16px"
      @row-click="selectPatient"
    >
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="mrn" label="MRN" width="120" />
      <el-table-column prop="name" label="Given Name" />
      <el-table-column prop="familyName" label="Family Name" />
      <el-table-column prop="birthDate" label="Birth Date" width="130" />
      <el-table-column prop="gender" label="Gender" width="100" />
      <el-table-column label="Action" width="120">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click.stop="selectPatient(row)">
            Select
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
