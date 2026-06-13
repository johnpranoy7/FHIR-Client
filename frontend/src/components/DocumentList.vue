<script setup>
import { ref, watch } from 'vue'
import { downloadBinary, getDocumentsByPatient } from '../api/fhirApi'
import { useMediaQuery } from '../composables/useMediaQuery'

const props = defineProps({
  patient: {
    type: Object,
    default: null
  }
})

const documents = ref([])
const loading = ref(false)
const error = ref('')
const downloadingId = ref(null)
const isNarrowViewport = useMediaQuery('(max-width: 1130px)')

watch(
  () => props.patient,
  async (patient) => {
    documents.value = []
    error.value = ''

    if (!patient?.id) {
      return
    }

    loading.value = true
    try {
      documents.value = await getDocumentsByPatient(patient.id)
      if (documents.value.length === 0) {
        error.value = 'No documents found for this patient.'
      }
    } catch (err) {
      error.value = 'Failed to load documents.'
      documents.value = []
    } finally {
      loading.value = false
    }
  },
  { immediate: true }
)

async function handleDownload(document) {
  if (!document.binaryId) {
    return
  }

  downloadingId.value = document.id
  try {
    const response = await downloadBinary(document.binaryId)
    const contentType = response.headers['content-type'] || document.contentType || 'application/octet-stream'
    const extension = contentType.includes('pdf') ? 'pdf' : 'bin'
    const blob = new Blob([response.data], { type: contentType })
    const url = window.URL.createObjectURL(blob)
    const link = window.document.createElement('a')
    link.href = url
    link.download = `${document.title || document.id}.${extension}`
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (err) {
    error.value = `Failed to download document ${document.id}.`
  } finally {
    downloadingId.value = null
  }
}
</script>

<template>
  <section class="documents-section card" :class="{ active: patient }">
    <div class="documents-header">
      <div>
        <h2>Patient Documents</h2>
        <p v-if="patient">
          Clinical documents linked to
          <strong>{{ patient.name }} {{ patient.familyName }}</strong>
        </p>
        <p v-else>Select a patient above to view their documents.</p>
      </div>
      <el-tag v-if="patient && documents.length" type="primary" effect="plain">
        {{ documents.length }} document{{ documents.length === 1 ? '' : 's' }}
      </el-tag>
    </div>

    <el-empty
      v-if="!patient"
      class="documents-empty"
      description="No patient selected"
    >
      <template #image>
        <div class="empty-icon">📄</div>
      </template>
    </el-empty>

    <template v-else>
      <el-alert v-if="error" :title="error" type="warning" show-icon :closable="false" />

      <div class="table-scroll">
        <el-table
          v-loading="loading"
          :data="documents"
          stripe
          class="documents-table"
          empty-text="No documents available"
        >
          <el-table-column v-if="!isNarrowViewport" prop="id" label="Document ID" width="140" />
          <el-table-column prop="title" label="Title" :min-width="isNarrowViewport ? 140 : 180" />
          <el-table-column v-if="!isNarrowViewport" prop="contentType" label="Content Type" min-width="160" />
          <el-table-column v-if="!isNarrowViewport" label="Downloadable" width="130">
            <template #default="{ row }">
              <el-tag :type="row.downloadable ? 'success' : 'info'" effect="light">
                {{ row.downloadable ? 'Yes' : 'View only' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Action" :width="isNarrowViewport ? 110 : 140">
            <template #default="{ row }">
              <el-button
                v-if="row.downloadable && row.binaryId"
                size="small"
                type="primary"
                :loading="downloadingId === row.id"
                @click="handleDownload(row)"
              >
                Download
              </el-button>
              <el-text v-else type="info">N/A</el-text>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </section>
</template>

<style scoped>
.documents-section {
  flex: 1;
  min-height: 420px;
  display: flex;
  flex-direction: column;
}

.documents-section.active {
  border-color: var(--app-border-strong);
  box-shadow:
    0 0 0 1px rgba(34, 211, 238, 0.08),
    0 24px 60px rgba(0, 0, 0, 0.35);
}

.documents-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.documents-header h2 {
  margin: 0 0 6px;
}

.documents-header p {
  margin: 0;
  color: var(--app-text-muted);
  font-size: 0.92rem;
}

.documents-header strong {
  color: var(--app-accent);
}

.documents-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 3.5rem;
  filter: grayscale(0.3);
  opacity: 0.7;
}

.documents-table {
  width: 100%;
  flex: 1;
}

@media (max-width: 767px) {
  .documents-section {
    min-height: 320px;
  }

  .documents-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
