
let allActors = [];

// DOM elements
const nameSearch = document.getElementById('nameSearch');
const genderSelect = document.getElementById('genderSelect');
const actorsGrid = document.getElementById('actorsGrid');
const loadingContainer = document.getElementById('loadingContainer');
const resultsCount = document.getElementById('resultsCount');
const modal = document.getElementById('actorModal');
const modalTitle = document.getElementById('modalTitle');
const openBtn = document.getElementById('openModalBtn');
const closeBtn = document.querySelector('.close');
const cancelBtn = document.getElementById('cancelBtn');
const form = document.getElementById('actorForm');
const alertContainer = document.getElementById('alertContainer');
const formLoading = document.querySelector('.form-loading');
const formLoadingText = document.getElementById('formLoadingText');
const submitBtn = document.getElementById('submitBtn');
const actorIDInput = document.getElementById('actorID');
const rankGroup = document.getElementById('rankGroup');

let currentActors = allActors;
let isUpdateMode = false;

const FALLBACK_ACTOR_IMAGE = 'https://via.placeholder.com/200x200?text=No+Photo'; // Consistent with movie page placeholder style

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadActorsFromBackend();
});

// Load actors from backend
async function loadActorsFromBackend(name = '', gender = '') {
    showLoading(true);
    
    try {
        let url = '/actors'; // Assuming this endpoint provides all actor data
        const params = new URLSearchParams();
        if (name) params.append('name', name);
        if (gender) params.append('gender', gender);
        if (params.toString()) url += `?${params.toString()}`;
        
        const response = await fetch(url, {
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (response.ok) {
            allActors = await response.json();
            currentActors = allActors; // Reset currentActors to full list after new fetch
            loadActors(); // Display actors based on fetched data
            updateResultsCount(name, gender);
        } else {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    } catch (error) {
        console.error('Error loading actors:', error);
        showError(`Không thể tải diễn viên: ${error.message}. Vui lòng kiểm tra kết nối và thử lại.`);
    } finally {
        showLoading(false);
    }
}

// Name search functionality
nameSearch.addEventListener('input', debounce(function() {
    const searchTerm = this.value.trim();
    loadActorsFromBackend(searchTerm, genderSelect.value); // Re-fetch based on filter
}, 300));

// Gender filter functionality
genderSelect.addEventListener('change', function() {
    const selectedGender = this.value;
    loadActorsFromBackend(nameSearch.value.trim(), selectedGender); // Re-fetch based on filter
});

// Load and display actors
function loadActors() {
    if (!currentActors || currentActors.length === 0) {
        actorsGrid.innerHTML = `
            <div class="no-actors">
                <h3>Không tìm thấy diễn viên</h3>
                <p>Hãy thử điều chỉnh tìm kiếm hoặc thêm một vài diễn viên!</p>
            </div>
        `;
        return;
    }

    actorsGrid.innerHTML = currentActors.map(actor => `
        <div class="actor-card">
            <img src="${actor.imageURL || FALLBACK_ACTOR_IMAGE}" 
                alt="${actor.name || 'Không có tên'}" 
                class="actor-image"
                onerror="this.src='${FALLBACK_ACTOR_IMAGE}'">
            
            <div class="actor-info">
                <h3 class="actor-name">${actor.name || 'Không có tên'}</h3>
                <div class="actor-meta">
                    <p><strong>ID:</strong> ${actor.actorID || 'N/A'}</p>
                    ${actor.gender ? `<p><strong>Giới tính:</strong> ${convertGender(actor.gender)}</p>` : ''}
                    ${actor.dob ? `<p><strong>Ngày sinh:</strong> ${formatDate(actor.dob)}</p>` : ''}
                    ${actor.rank != null ? `<p><strong>Thứ hạng:</strong> ${actor.rank}</p>` : ''}
                    ${actor.bio ? `<p><strong>Tiểu sử:</strong> ${actor.bio}</p>` : ''}
                </div>
                <button class="update-btn" onclick="openUpdateModal(${actor.actorID})" aria-label="Cập nhật ${actor.name}">Cập nhật</button>
            </div>
        </div>
    `).join('');
}

// Helper to convert English gender to Vietnamese
function convertGender(gender) {
    switch (gender) {
        case 'Male': return 'Nam';
        case 'Female': return 'Nữ';
        case 'Other': return 'Khác';
        default: return gender;
    }
}

// Open update modal with pre-filled data
function openUpdateModal(actorID) {
    const actor = currentActors.find(a => a.actorID === actorID);
    if (!actor) {
        showAlert('Không tìm thấy diễn viên.', 'error');
        return;
    }

    isUpdateMode = true;
    modalTitle.textContent = 'Cập Nhật Diễn Viên';
    submitBtn.textContent = 'Cập Nhật Diễn Viên';
    submitBtn.setAttribute('aria-label', 'Cập Nhật Diễn Viên');
    formLoadingText.textContent = 'Đang cập nhật diễn viên...';
    rankGroup.style.display = 'block'; // Ensure rank field is visible for update

    // Populate form fields
    document.getElementById('actorID').value = actor.actorID || '';
    document.getElementById('name').value = actor.name || '';
    document.getElementById('imageURL').value = actor.imageURL || '';
    document.getElementById('gender').value = actor.gender || '';
    document.getElementById('dob').value = actor.dob ? new Date(actor.dob).toISOString().split('T')[0] : '';
    document.getElementById('rank').value = actor.rank != null ? actor.rank : '';
    document.getElementById('bio').value = actor.bio || '';

    modal.style.display = 'flex'; // Use flex to center modal
    document.body.style.overflow = 'hidden';
    alertContainer.innerHTML = ''; // Clear any previous alerts
    formLoading.style.display = 'none'; // Hide form loading initially
    submitBtn.disabled = false; // Enable submit button
}

// Show error message for main grid
function showError(message) {
    actorsGrid.innerHTML = `
        <div class="error-container">
            <h3>⚠️ Lỗi Tải Diễn Viên</h3>
            <p>${message}</p>
            <button onclick="loadActorsFromBackend()" class="btn btn-primary" style="margin-top: 15px;">
                Thử lại
            </button>
        </div>
    `;
}

// Update results count
function updateResultsCount(name, gender) {
    const count = currentActors.length;
    let filterText = '';
    if (name && gender) {
        filterText = ` khớp với "${name}" và giới tính "${convertGender(gender)}"`;
    } else if (name) {
        filterText = ` khớp với "${name}"`;
    } else if (gender) {
        filterText = ` giới tính "${convertGender(gender)}"`;
    }
    resultsCount.textContent = `Tìm thấy ${count} diễn viên${count !== 1 ? '' : ''}${filterText}`;
    resultsCount.style.display = 'block';
}

// Show/hide loading for main grid
function showLoading(show) {
    loadingContainer.style.display = show ? 'block' : 'none';
    actorsGrid.style.display = show ? 'none' : 'grid'; // Ensure actorsGrid is visible when not loading
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    if (isNaN(date)) return 'Ngày không hợp lệ';
    return date.toLocaleDateString('vi-VN', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Modal functionality
openBtn.addEventListener('click', () => {
    isUpdateMode = false;
    modalTitle.textContent = 'Thêm Diễn Viên Mới';
    submitBtn.textContent = 'Thêm Diễn Viên';
    submitBtn.setAttribute('aria-label', 'Thêm Diễn Viên');
    formLoadingText.textContent = 'Đang thêm diễn viên...';
    rankGroup.style.display = 'block'; // Ensure rank field is visible for new entry
    form.reset();
    modal.style.display = 'flex'; // Use flex to center modal
    document.body.style.overflow = 'hidden';
    alertContainer.innerHTML = ''; // Clear any previous alerts
    formLoading.style.display = 'none'; // Hide form loading initially
    submitBtn.disabled = false; // Enable submit button
});

function closeModal() {
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
    form.reset();
    alertContainer.innerHTML = '';
    formLoading.style.display = 'none';
    submitBtn.disabled = false;
    isUpdateMode = false;
    rankGroup.style.display = 'block'; // Reset rank field display on close
}

closeBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);

window.addEventListener('click', (e) => {
    if (e.target === modal) {
        closeModal();
    }
});

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && modal.style.display === 'flex') { // Check for 'flex'
        closeModal();
    }
});

// Form submission
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Client-side validation
    const name = document.getElementById('name').value.trim();
    const imageURL = document.getElementById('imageURL').value.trim();
    if (!name) {
        showAlert('Tên diễn viên là bắt buộc.', 'error');
        return;
    }
    if (!imageURL) {
        showAlert('URL Ảnh chân dung là bắt buộc.', 'error');
        return;
    }
    try {
        new URL(imageURL); // Validate URL format
    } catch (err) {
        showAlert('URL Ảnh chân dung không hợp lệ.', 'error');
        return;
    }


    formLoading.style.display = 'block';
    submitBtn.disabled = true;
    alertContainer.innerHTML = '';

    const formData = new FormData(form);
    const actorID = formData.get('actorID');
    const url = isUpdateMode ? `/manager/actors/${actorID}` : '/manager/actors';
    const method = isUpdateMode ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });

        const result = await response.json();
        
        formLoading.style.display = 'none';
        submitBtn.disabled = false;

        if (result.success) {
            showAlert(result.message, 'success');
            setTimeout(() => {
                closeModal();
                loadActorsFromBackend(nameSearch.value.trim(), genderSelect.value);
            }, 1500);
        } else {
            showAlert(result.message, 'error');
        }
    } catch (error) {
        formLoading.style.display = 'none';
        submitBtn.disabled = false;
        showAlert(`Có lỗi xảy ra khi ${isUpdateMode ? 'cập nhật' : 'thêm'} diễn viên: ${error.message}.`, 'error');
        console.error('Error:', error);
    }
});

// Show alert function for modal
function showAlert(message, type) {
    alertContainer.innerHTML = `
        <div class="alert alert-${type}">
            ${message}
        </div>
    `;
    alertContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// Debounce function to limit API calls during typing
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func.apply(this, args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}