// app.js
import { ref } from 'vue';
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL + '/api/barcode';

// 상태
export const selected = ref('');
export const barcodeNumber = ref('');
export const uniqueKey = ref('');
export const barcodeData = ref(null);
export const message = ref('');
export const messageType = ref('success');
export const loading = ref(false);

// 메시지 표시
export const showMessage = (text, type) => {
  message.value = text;
  messageType.value = type;
  setTimeout(() => {
    message.value = '';
  }, 5000);
};

// 바코드 생성
export const createBarcode = async () => {
  if (!barcodeNumber.value.trim()) {
    showMessage('바코드 번호를 입력해주세요.', 'error');
    return;
  }

  loading.value = true;
  message.value = '';

  try {
    const response = await axios.post(`${API_URL}/create`, {
      barcodeNumber: barcodeNumber.value,
      uniqueKey: uniqueKey.value || null
    });

    barcodeData.value = response.data;
    uniqueKey.value = response.data.uniqueKey;
    showMessage(response.data.message, 'success');
  } catch (error) {
    showMessage(
      '오류가 발생했습니다: ' + (error.response?.data?.message || error.message),
      'error'
    );
  } finally {
    loading.value = false;
  }
};

// 바코드 다운로드
export const downloadBarcode = async () => {
  if (!uniqueKey.value) return;

  try {
    const response = await axios.get(`${API_URL}/download/${uniqueKey.value}`, {
      responseType: 'blob'
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `barcode_${barcodeData.value.barcodeNumber}.png`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);

    await refreshBarcodeInfo();
    showMessage('바코드 이미지가 다운로드되었습니다.', 'success');
  } catch (error) {
    showMessage('다운로드 중 오류가 발생했습니다.', 'error');
  }
};

// 바코드 정보 새로고침
export const refreshBarcodeInfo = async () => {
  try {
    const response = await axios.get(`${API_URL}/info/${uniqueKey.value}`);
    barcodeData.value = response.data;
  } catch (error) {
    console.error('바코드 정보 새로고침 실패:', error);
  }
};

// 폼 리셋
export const resetForm = () => {
  barcodeNumber.value = '';
  uniqueKey.value = '';
  barcodeData.value = null;
  message.value = '';
};
