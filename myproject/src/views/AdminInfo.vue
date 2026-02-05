<template>
  <div>
    <p>
      관리자 이름:
      <input
        :value="userName"
        @input="$emit('update:userName', $event.target.value)"
        placeholder="이름을 입력하세요"
        class="input"
      />
      <button @click="$emit('show-name')" class="btn btn-primary">확인</button>
    </p>
    <div v-if="message" class="welcome-message">
      안녕하세요 {{ message }}님! 상품 관리 시스템을 시작합니다.
    </div>
  </div>
</template>

<script setup>
defineProps({
  userName: String,
  message: String,
});

defineEmits(["update:userName", "show-name"]);
</script>
