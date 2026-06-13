<script setup>
import { ref, watch } from 'vue'
import { downloadBinary, getDocumentsByPatient } from '../api/fhirApi'

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
  <div class="card">
    <h2>Patient Documents</h2>

    <el-empty v-if="!patient" description="Select a patient to view documents." />

    <template v-else>
      <div class="selected-patient">
        <el-tag type="info">
          {{ patient.name }} {{ patient.familyName }} (ID: {{ patient.id }})
        </el-tag>
      </div>

      <el-alert v-if="error" :title="error" type="warning" show-icon :closable="false" />

      <el-table
        v-loading="loading"
        :data="documents"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="Document ID" width="140" />
        <el-table-column prop="title" label="Title" min-width="180" />
        <el-table-column prop="contentType" label="Content Type" min-width="180" />
        <el-table-column label="Downloadable" width="130">
          <template #default="{ row }">
            <el-tag :type="row.downloadable ? 'success' : 'info'">
              {{ row.downloadable ? 'Yes' : 'View only' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Action" width="140">
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
    </template>
  </div>
</template>
