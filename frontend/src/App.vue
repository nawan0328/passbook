<template>
  <div id="app">
    <div class="container">
		<h1>
		  <img src="/favicon.png" alt="icon" class="title-icon" />
		  바코드 생성기
		</h1>
      <p class="subtitle">C128 바코드를 생성하고 다운로드하세요</p>
      
      <div class="card">
        <div class="form-group">
          <label for="barcodeNumber">바코드 번호</label>
          <input
            type="text"
            id="barcodeNumber"
            v-model="barcodeNumber"
            placeholder="바코드 번호를 입력하세요"
            class="input"
          />
        </div>
        
        <div class="form-group" v-if="uniqueKey">
          <label for="uniqueKey">유니크 키 (수정시)</label>
          <input
            type="text"
            id="uniqueKey"
            v-model="uniqueKey"
            placeholder="기존 바코드를 수정하려면 유니크 키를 입력하세요"
            class="input"
            readonly
          />
        </div>
        
        <div class="button-group">
          <button @click="createBarcode" class="btn btn-primary" :disabled="loading">
            {{ loading ? '처리중...' : (uniqueKey ? '바코드 수정' : '바코드 생성') }}
          </button>
          
          <button 
            v-if="uniqueKey" 
            @click="resetForm" 
            class="btn btn-secondary"
          >
            새로 만들기
          </button>
        </div>
        
        <div v-if="message" :class="['message', messageType]">
          {{ message }}
        </div>
        
        <div v-if="barcodeData" class="result-section">
          <h3>✅ 생성 완료</h3>
          
          <div class="info-box">
            <div class="info-item">
              <span class="label">유니크 키:</span>
              <span class="value">{{ barcodeData.uniqueKey }}</span>
            </div>
            <div class="info-item">
              <span class="label">바코드 번호:</span>
              <span class="value">{{ barcodeData.barcodeNumber }}</span>
            </div>
            <div class="info-item">
              <span class="label">다운로드 횟수:</span>
              <span class="value">{{ barcodeData.downloadCount }}회</span>
            </div>
          </div>
          
          <button @click="downloadBarcode" class="btn btn-download">
            📥 바코드 이미지 다운로드
          </button>
          
          <p class="note">
            * 유니크 키를 저장해두시면 나중에 바코드를 수정할 수 있습니다.
          </p>
        </div>
      </div>
      
      <div class="card" v-if="!barcodeData">
        <h3>📋 사용 방법</h3>
        <ul class="instructions">
          <li>바코드 번호를 입력하고 '바코드 생성' 버튼을 클릭하세요</li>
          <li>생성된 유니크 키를 저장해두세요</li>
          <li>바코드 이미지를 다운로드할 수 있습니다</li>
          <li>유니크 키로 나중에 바코드를 수정할 수 있습니다</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL + '/api/barcode';

export default {
  name: 'App',
  data() {
    return {
      barcodeNumber: '',
      uniqueKey: '',
      barcodeData: null,
      message: '',
      messageType: 'success',
      loading: false
    };
  },
  methods: {
    async createBarcode() {
      if (!this.barcodeNumber.trim()) {
        this.showMessage('바코드 번호를 입력해주세요.', 'error');
        return;
      }
      
      this.loading = true;
      this.message = '';
      
      try {
        const response = await axios.post(`${API_URL}/create`, {
          barcodeNumber: this.barcodeNumber,
          uniqueKey: this.uniqueKey || null
        });
        
        this.barcodeData = response.data;
        this.uniqueKey = response.data.uniqueKey;
        this.showMessage(response.data.message, 'success');
      } catch (error) {
        this.showMessage('오류가 발생했습니다: ' + (error.response?.data?.message || error.message), 'error');
      } finally {
        this.loading = false;
      }
    },
    
    async downloadBarcode() {
      if (!this.uniqueKey) return;
      
      try {
        const response = await axios.get(`${API_URL}/download/${this.uniqueKey}`, {
          responseType: 'blob'
        });
        
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `barcode_${this.barcodeData.barcodeNumber}.png`);
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
        
        // 다운로드 카운트 새로고침
        await this.refreshBarcodeInfo();
        
        this.showMessage('바코드 이미지가 다운로드되었습니다.', 'success');
      } catch (error) {
        this.showMessage('다운로드 중 오류가 발생했습니다.', 'error');
      }
    },
    
    async refreshBarcodeInfo() {
      try {
        const response = await axios.get(`${API_URL}/info/${this.uniqueKey}`);
        this.barcodeData = response.data;
      } catch (error) {
        console.error('바코드 정보 새로고침 실패:', error);
      }
    },
    
    resetForm() {
      this.barcodeNumber = '';
      this.uniqueKey = '';
      this.barcodeData = null;
      this.message = '';
    },
    
    showMessage(msg, type) {
      this.message = msg;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 5000);
    }
  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background: radial-gradient(circle at bottom right, #3cb371 0%, #ffffff 30%);
  min-height: 100vh;
  padding: 20px;
}

#app {
  max-width: 100%;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.title-icon {
  width: 80px;      /* 아이콘 크기 */
  height: 80px;
  vertical-align: middle; /* 글자와 수평 정렬 */
  margin-right: 4px;       /* 글자와 간격 */
}

h1 {
  color: black;
  text-align: center;
  margin-bottom: 10px;
  font-size: 2.5rem;
}

.subtitle {
  color: rgba(0, 0, 0, 0.9);
  text-align: center;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.input:focus {
  outline: none;
  border-color: #667eea;
}

.input:readonly {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.btn {
  flex: 1;
  padding: 14px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.btn-download {
  width: 100%;
  background: #4caf50;
  color: white;
  margin-top: 20px;
}

.btn-download:hover {
  background: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(76, 175, 80, 0.4);
}

.message {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-weight: 500;
}

.message.success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.message.error {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}

.result-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 2px solid #f0f0f0;
}

.result-section h3 {
  color: #333;
  margin-bottom: 20px;
  font-size: 1.5rem;
}

.info-box {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 600;
  color: #666;
}

.info-item .value {
  color: #333;
  font-family: monospace;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
}

.note {
  color: #666;
  font-size: 0.9rem;
  margin-top: 15px;
  font-style: italic;
}

.instructions {
  list-style: none;
  padding: 0;
}

.instructions li {
  padding: 12px 0;
  padding-left: 30px;
  position: relative;
  color: #555;
  line-height: 1.6;
}

.instructions li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
  font-size: 18px;
}

@media (max-width: 800px) {
  h1 {
    font-size: 2rem;
  }
  
  .card {
    padding: 20px;
  }
  
  .button-group {
    flex-direction: column;
  }
}
</style>