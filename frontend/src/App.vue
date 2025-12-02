<script setup>
import {
  selected,
  barcodeNumber,
  uniqueKey,
  barcodeData,
  message,
  messageType,
  loading,
  createBarcode,
  downloadBarcode,
  resetForm
} from './scripts/app.js';
</script>
<style src="./assets/app.css"></style>

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
          <label for="membershipSelected">선택됨: {{ selected }}</label>
          <select v-model="selected" class="input" required>
            <option value="" disabled selected hidden>하나를 선택하세요</option>
            <option value="A">A</option>
            <option value="B">B</option>
            <option value="C">C</option>
          </select>
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
		
		<!-- 선택 값에 따라 다른 view 보여주기 -->
		<div v-if="selected === 'A'">
		  <p>사용자 A를 선택했습니다!</p>
		</div>
		<div v-else-if="selected === 'B'">
		  <p>사용자 B를 선택했습니다!</p>
		</div>
		<div v-else-if="selected === 'C'">
		  <p>사용자 C를 선택했습니다!</p>
		</div>

      </div>
    </div>
  </div>
</template>

