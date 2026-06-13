import { ref, onMounted, onUnmounted } from 'vue'

export function useMediaQuery(query) {
  const matches = ref(false)
  let mediaQuery

  function update() {
    matches.value = mediaQuery?.matches ?? false
  }

  onMounted(() => {
    mediaQuery = window.matchMedia(query)
    update()
    mediaQuery.addEventListener('change', update)
  })

  onUnmounted(() => {
    mediaQuery?.removeEventListener('change', update)
  })

  return matches
}
