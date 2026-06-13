<script setup>
import { ref, watch } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { searchPatients } from '../api/fhirApi'

const props = defineProps({
  selectedPatient: {
    type: Object,
    default: null
  },
  expanded: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['select', 'expand', 'collapse', 'clear'])

const givenName = ref('')
const familyName = ref('')
const patients = ref([])
const loading = ref(false)
const error = ref('')

watch(
  () => props.selectedPatient,
  (patient) => {
    if (patient) {
      givenName.value = patient.name || ''
      familyName.value = patient.familyName || ''
    }
  }
)

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
  emit('collapse')
}

function searchDifferentPatient() {
  patients.value = []
  error.value = ''
  emit('clear')
  emit('expand')
}

function rowClassName({ row }) {
  return row.id === props.selectedPatient?.id ? 'selected-row' : ''
}
</script>

<template>
  <section class="patient-panel" :class="{ collapsed: selectedPatient && !expanded }">
    <!-- Collapsed bar -->
    <div v-if="selectedPatient && !expanded" class="patient-bar">
      <button class="patient-bar-main" type="button" @click="emit('expand')">
        <div class="patient-bar-icon">
          <span>{{ (selectedPatient.name || '?').charAt(0).toUpperCase() }}</span>
        </div>
        <div class="patient-bar-info">
          <span class="patient-bar-label">Selected Patient</span>
          <span class="patient-bar-name">
            {{ selectedPatient.name || '—' }} {{ selectedPatient.familyName || '' }}
          </span>
          <span class="patient-bar-id">ID: {{ selectedPatient.id }}</span>
        </div>
        <el-icon class="expand-icon"><ArrowDown /></el-icon>
      </button>
      <el-button plain size="small" @click="searchDifferentPatient">
        Change Patient
      </el-button>
    </div>

    <!-- Expanded search -->
    <div v-else class="patient-search card">
      <div class="panel-header">
        <div>
          <h2>Patient Search</h2>
          <p>Find a patient by given name, family name, or both.</p>
        </div>
        <el-button
          v-if="selectedPatient"
          text
          type="primary"
          @click="emit('collapse')"
        >
          Collapse
        </el-button>
      </div>

      <div v-if="selectedPatient" class="current-patient-banner">
        <div class="banner-content">
          <span class="banner-badge">Current</span>
          <strong>{{ selectedPatient.name }} {{ selectedPatient.familyName }}</strong>
          <span class="banner-meta">
            ID {{ selectedPatient.id }}
            <template v-if="selectedPatient.mrn"> · MRN {{ selectedPatient.mrn }}</template>
            <template v-if="selectedPatient.birthDate"> · {{ selectedPatient.birthDate }}</template>
            <template v-if="selectedPatient.gender"> · {{ selectedPatient.gender }}</template>
          </span>
        </div>
        <el-button size="small" @click="searchDifferentPatient">
          Search Different Patient
        </el-button>
      </div>

      <el-form inline class="search-form" @submit.prevent="handleSearch">
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

      <el-alert
        v-if="error"
        :title="error"
        type="warning"
        show-icon
        :closable="false"
        class="search-alert"
      />

      <el-table
        v-if="patients.length"
        :data="patients"
        :row-class-name="rowClassName"
        stripe
        class="patient-table"
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
            <el-button
              size="small"
              :type="row.id === selectedPatient?.id ? 'success' : 'primary'"
              @click.stop="selectPatient(row)"
            >
              {{ row.id === selectedPatient?.id ? 'Selected' : 'Select' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<style scoped>
.patient-panel {
  transition: all 0.25s ease;
}

.patient-panel.collapsed {
  margin-bottom: 16px;
}

.patient-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%);
  color: #fff;
  border-radius: 14px;
  padding: 12px 16px;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.25);
}

.patient-bar-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  text-align: left;
  padding: 0;
}

.patient-bar-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.1rem;
}

.patient-bar-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.patient-bar-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  opacity: 0.8;
}

.patient-bar-name {
  font-size: 1.05rem;
  font-weight: 600;
}

.patient-bar-id {
  font-size: 0.85rem;
  opacity: 0.85;
}

.expand-icon {
  margin-left: auto;
  font-size: 1.1rem;
  opacity: 0.85;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.panel-header h2 {
  margin: 0 0 4px;
}

.panel-header p {
  margin: 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.current-patient-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: #ecfdf5;
  border: 1px solid #a7f3d0;
  border-radius: 10px;
  padding: 12px 16px;
  margin-bottom: 20px;
}

.banner-content {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.banner-badge {
  background: #059669;
  color: #fff;
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 3px 8px;
  border-radius: 999px;
}

.banner-meta {
  color: #047857;
  font-size: 0.88rem;
}

.search-form {
  margin-bottom: 8px;
}

.search-alert {
  margin-bottom: 16px;
}

.patient-table {
  width: 100%;
  margin-top: 8px;
}

:deep(.selected-row) {
  --el-table-tr-bg-color: #eff6ff;
}

:deep(.selected-row td) {
  font-weight: 600;
}
</style>
