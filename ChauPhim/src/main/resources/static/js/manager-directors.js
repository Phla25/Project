
        let allDirectors = []; // Changed from allActors

        // DOM elements (updated names for directors)
        const nameSearch = document.getElementById('nameSearch');
        const genderSelect = document.getElementById('genderSelect');
        const directorsGrid = document.getElementById('directorsGrid'); // Changed from actorsGrid
        const loadingContainer = document.getElementById('loadingContainer');
        const resultsCount = document.getElementById('resultsCount');
        const modal = document.getElementById('directorModal'); // Changed from actorModal
        const modalTitle = document.getElementById('modalTitle');
        const openBtn = document.getElementById('openModalBtn');
        const closeBtn = document.querySelector('.close');
        const cancelBtn = document.getElementById('cancelBtn');
        const form = document.getElementById('directorForm'); // Changed from actorForm
        const alertContainer = document.getElementById('alertContainer');
        const formLoading = document.querySelector('.form-loading');
        const formLoadingText = document.getElementById('formLoadingText');
        const submitBtn = document.getElementById('submitBtn');
        const directorIDInput = document.getElementById('directorID'); // Changed from actorID
        const rankGroup = document.getElementById('rankGroup');

        let currentDirectors = allDirectors; // Changed from currentActors
        let isUpdateMode = false;

        const FALLBACK_DIRECTOR_IMAGE = 'https://via.placeholder.com/200x200?text=No+Photo'; // Changed from FALLBACK_ACTOR_IMAGE

        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            loadDirectorsFromBackend(); // Changed function name
        });

        // Load directors from backend (changed function name and endpoint)
        async function loadDirectorsFromBackend(name = '', gender = '') {
            showLoading(true);
            
            try {
                let url = '/directors'; // Changed endpoint from /actors
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
                    allDirectors = await response.json(); // Changed from allActors
                    currentDirectors = allDirectors; // Reset currentDirectors to full list after new fetch
                    loadDirectors(); // Changed function name to loadDirectors
                    updateResultsCount(name, gender);
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            } catch (error) {
                console.error('Error loading directors:', error); // Updated console log
                showError(`Không thể tải đạo diễn: ${error.message}. Vui lòng kiểm tra kết nối và thử lại.`); // Updated error message
            } finally {
                showLoading(false);
            }
        }

        // Name search functionality
        nameSearch.addEventListener('input', debounce(function() {
            const searchTerm = this.value.trim();
            loadDirectorsFromBackend(searchTerm, genderSelect.value); // Changed function name
        }, 300));

        // Gender filter functionality
        genderSelect.addEventListener('change', function() {
            const selectedGender = this.value;
            loadDirectorsFromBackend(nameSearch.value.trim(), selectedGender); // Changed function name
        });

        // Load and display directors (changed function name and variable names)
        function loadDirectors() {
            if (!currentDirectors || currentDirectors.length === 0) {
                directorsGrid.innerHTML = `
                    <div class="no-directors">
                        <h3>Không tìm thấy đạo diễn</h3>
                        <p>Hãy thử điều chỉnh tìm kiếm hoặc thêm một vài đạo diễn!</p>
                    </div>
                `;
                return;
            }

            directorsGrid.innerHTML = currentDirectors.map(director => `
                <div class="director-card">
                    <img src="${director.imageURL || FALLBACK_DIRECTOR_IMAGE}" 
                        alt="${director.name || 'Không có tên'}" 
                        class="director-image"
                        onerror="this.src='${FALLBACK_DIRECTOR_IMAGE}'">
                    
                    <div class="director-info">
                        <h3 class="director-name">${director.name || 'Không có tên'}</h3>
                        <div class="director-meta">
                            <p><strong>ID:</strong> ${director.directorID || 'N/A'}</p> ${director.gender ? `<p><strong>Giới tính:</strong> ${convertGender(director.gender)}</p>` : ''}
                            ${director.dob ? `<p><strong>Ngày sinh:</strong> ${formatDate(director.dob)}</p>` : ''}
                            ${director.bio ? `<p><strong>Tiểu sử:</strong> ${director.bio}</p>` : ''}
                        </div>
                        <button class="update-btn" onclick="openUpdateModal(${director.directorID})" aria-label="Cập nhật ${director.name}">Cập nhật</button>
                    </div>
                </div>
            `).join('');
        }

        // Helper to convert English gender to Vietnamese (remains the same)
        function convertGender(gender) {
            switch (gender) {
                case 'Male': return 'Nam';
                case 'Female': return 'Nữ';
                case 'Other': return 'Khác';
                default: return gender;
            }
        }

        // Open update modal with pre-filled data (changed function name and variable names)
        function openUpdateModal(directorID) {
            const director = currentDirectors.find(d => d.directorID === directorID); // Changed find logic
            if (!director) {
                showAlert('Không tìm thấy đạo diễn.', 'error');
                return;
            }

            isUpdateMode = true;
            modalTitle.textContent = 'Cập Nhật Đạo Diễn';
            submitBtn.textContent = 'Cập Nhật Đạo Diễn';
            submitBtn.setAttribute('aria-label', 'Cập Nhật Đạo Diễn');
            formLoadingText.textContent = 'Đang cập nhật đạo diễn...';
            rankGroup.style.display = 'none'; // Hide rank field for directors

            // Populate form fields (updated ID names)
            document.getElementById('directorID').value = director.directorID || '';
            document.getElementById('name').value = director.name || '';
            document.getElementById('imageURL').value = director.imageURL || '';
            document.getElementById('gender').value = director.gender || '';
            document.getElementById('dob').value = director.dob ? new Date(director.dob).toISOString().split('T')[0] : '';
            // document.getElementById('rank').value = ''; // Ensure rank is empty for directors if it somehow appears
            document.getElementById('bio').value = director.bio || '';

            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
            alertContainer.innerHTML = '';
            formLoading.style.display = 'none';
            submitBtn.disabled = false;
        }

        // Show error message for main grid (updated message)
        function showError(message) {
            directorsGrid.innerHTML = `
                <div class="error-container">
                    <h3>⚠️ Lỗi Tải Đạo Diễn</h3>
                    <p>${message}</p>
                    <button onclick="loadDirectorsFromBackend()" class="btn btn-primary" style="margin-top: 15px;">
                        Thử lại
                    </button>
                </div>
            `;
        }

        // Update results count (updated message)
        function updateResultsCount(name, gender) {
            const count = currentDirectors.length;
            let filterText = '';
            if (name && gender) {
                filterText = ` khớp với "${name}" và giới tính "${convertGender(gender)}"`;
            } else if (name) {
                filterText = ` khớp với "${name}"`;
            } else if (gender) {
                filterText = ` giới tính "${convertGender(gender)}"`;
            }
            resultsCount.textContent = `Tìm thấy ${count} đạo diễn${count !== 1 ? '' : ''}${filterText}`;
            resultsCount.style.display = 'block';
        }

        // Show/hide loading for main grid (remains the same, just target changed)
        function showLoading(show) {
            loadingContainer.style.display = show ? 'block' : 'none';
            directorsGrid.style.display = show ? 'none' : 'grid'; // Ensure directorsGrid is visible when not loading
        }

        // Format date (remains the same)
        function formatDate(dateString) {
            const date = new Date(dateString);
            if (isNaN(date)) return 'Ngày không hợp lệ';
            return date.toLocaleDateString('vi-VN', { 
                year: 'numeric', 
                month: 'short', 
                day: 'numeric' 
            });
        }

        // Modal functionality (updated text and target element names)
        openBtn.addEventListener('click', () => {
            isUpdateMode = false;
            modalTitle.textContent = 'Thêm Đạo Diễn Mới';
            submitBtn.textContent = 'Thêm Đạo Diễn';
            submitBtn.setAttribute('aria-label', 'Thêm Đạo Diễn');
            formLoadingText.textContent = 'Đang thêm đạo diễn...';
            rankGroup.style.display = 'none'; // Hide rank field for new director entry
            form.reset();
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
            alertContainer.innerHTML = '';
            formLoading.style.display = 'none';
            submitBtn.disabled = false;
        });

        function closeModal() {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
            form.reset();
            alertContainer.innerHTML = '';
            formLoading.style.display = 'none';
            submitBtn.disabled = false;
            isUpdateMode = false;
            rankGroup.style.display = 'none'; // Ensure rank field remains hidden on close
        }

        closeBtn.addEventListener('click', closeModal);
        cancelBtn.addEventListener('click', closeModal);

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal();
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.style.display === 'flex') {
                closeModal();
            }
        });

        // Form submission (updated variable names, endpoint, and messages)
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Client-side validation
            const name = document.getElementById('name').value.trim();
            const imageURL = document.getElementById('imageURL').value.trim();
            if (!name) {
                showAlert('Tên đạo diễn là bắt buộc.', 'error'); // Updated message
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
            const directorID = formData.get('directorID'); // Changed ID name
            const url = isUpdateMode ? `/manager/directors/${directorID}` : '/manager/directors'; // Changed endpoint
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
                        loadDirectorsFromBackend(nameSearch.value.trim(), genderSelect.value); // Changed function name
                    }, 1500);
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                formLoading.style.display = 'none';
                submitBtn.disabled = false;
                showAlert(`Có lỗi xảy ra khi ${isUpdateMode ? 'cập nhật' : 'thêm'} đạo diễn: ${error.message}.`, 'error'); // Updated message
                console.error('Error:', error);
            }
        });

        // Show alert function for modal (remains the same)
        function showAlert(message, type) {
            alertContainer.innerHTML = `
                <div class="alert alert-${type}">
                    ${message}
                </div>
            `;
            alertContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }

        // Debounce function (remains the same)
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