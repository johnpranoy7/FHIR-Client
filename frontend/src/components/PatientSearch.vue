<script setup>
import { ref, watch } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { searchPatients } from '../api/fhirApi'
import { useMediaQuery } from '../composables/useMediaQuery'

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
const isNarrowViewport = useMediaQuery('(max-width: 1130px)')

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

function getPatientInitials(patient) {
  if (!patient) return '?'

  const given = (patient.name || '').trim().charAt(0).toUpperCase()
  const family = (patient.familyName || '').trim().charAt(0).toUpperCase()
  const initials = `${given}${family}`

  return initials || '?'
}
</script>

<template>
  <section class="patient-panel" :class="{ collapsed: selectedPatient && !expanded }">
    <!-- Collapsed bar -->
    <div v-if="selectedPatient && !expanded" class="patient-bar">
      <button class="patient-bar-main" type="button" @click="emit('expand')">
        <div class="patient-bar-icon">
          <span>{{ getPatientInitials(selectedPatient) }}</span>
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
      <el-button class="change-patient-btn" plain size="small" @click="searchDifferentPatient">
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
          <el-input v-model="givenName" placeholder="Ashley" clearable />
        </el-form-item>
        <el-form-item label="Family Name">
          <el-input v-model="familyName" placeholder="Young" clearable />
        </el-form-item>
        <el-form-item class="search-submit-item">
          <el-button type="primary" class="search-submit-btn" :loading="loading" @click="handleSearch">
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

      <div v-if="patients.length" class="table-scroll">
        <el-table
          :data="patients"
          :row-class-name="rowClassName"
          stripe
          class="patient-table"
          @row-click="selectPatient"
        >
          <el-table-column v-if="!isNarrowViewport" prop="id" label="ID" :width="isNarrowViewport ? 90 : 120" />
          <el-table-column v-if="!isNarrowViewport" prop="mrn" label="MRN" width="120" />
          <el-table-column prop="name" label="Given Name" :min-width="isNarrowViewport ? 100 : 120" />
          <el-table-column prop="familyName" label="Family Name" :min-width="isNarrowViewport ? 100 : 120" />
          <el-table-column v-if="!isNarrowViewport" prop="birthDate" label="Birth Date" width="130" />
          <el-table-column v-if="!isNarrowViewport" prop="gender" label="Gender" width="100" />
          <el-table-column label="Action" :width="isNarrowViewport ? 100 : 120">
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
  flex-wrap: wrap;
  background: linear-gradient(135deg, rgba(8, 145, 178, 0.35) 0%, rgba(99, 102, 241, 0.35) 100%);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--app-border-strong);
  color: var(--app-text);
  border-radius: var(--app-radius);
  padding: 14px 18px;
  box-shadow: 0 16px 40px rgba(34, 211, 238, 0.12);
}

.change-patient-btn {
  flex-shrink: 0;
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
  transition: opacity 0.2s;
}

.patient-bar-main:hover {
  opacity: 0.9;
}

.patient-bar-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(135deg, #0891b2, #6366f1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 1.1rem;
  color: #fff;
  box-shadow: 0 8px 20px rgba(34, 211, 238, 0.3);
}

.patient-bar-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.patient-bar-label {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--app-accent);
  font-weight: 700;
}

.patient-bar-name {
  font-size: 1.08rem;
  font-weight: 700;
  color: var(--app-text);
}

.patient-bar-id {
  font-size: 0.85rem;
  color: var(--app-text-muted);
}

.expand-icon {
  margin-left: auto;
  font-size: 1.1rem;
  color: var(--app-accent);
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
  color: var(--app-text-muted);
  font-size: 0.9rem;
}

.current-patient-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  background: var(--app-success-soft);
  border: 1px solid rgba(52, 211, 153, 0.28);
  border-radius: 14px;
  padding: 14px 18px;
  margin-bottom: 20px;
}

.banner-content {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.banner-content strong {
  color: var(--app-text);
}

.banner-badge {
  background: linear-gradient(135deg, #059669, #10b981);
  color: #fff;
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  padding: 4px 10px;
  border-radius: 999px;
}

.banner-meta {
  color: var(--app-success);
  font-size: 0.88rem;
}

.search-form {
  margin-bottom: 8px;
}

@media (max-width: 1130px) {
  .search-form {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }

  .search-form :deep(.el-form-item) {
    margin-right: 0;
    width: 100%;
  }

  .search-form :deep(.search-submit-item) {
    margin-bottom: 0;
  }

  .search-form :deep(.search-submit-item .el-form-item__content) {
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .search-submit-btn {
    width: 100%;
    max-width: 100%;
  }
}

.search-alert {
  margin-bottom: 16px;
}

.patient-table {
  width: 100%;
  margin-top: 8px;
}

.patient-table :deep(.el-table__row) {
  cursor: pointer;
}

:deep(.selected-row) {
  --el-table-tr-bg-color: rgba(34, 211, 238, 0.08) !important;
}

:deep(.selected-row td) {
  font-weight: 600;
  color: var(--app-accent) !important;
}

@media (max-width: 767px) {
  .patient-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .patient-bar-main {
    width: 100%;
  }

  .change-patient-btn {
    width: 100%;
  }

  .current-patient-banner {
    flex-direction: column;
    align-items: stretch;
  }

  .panel-header {
    flex-direction: column;
  }
}
</style>
