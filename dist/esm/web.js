import { WebPlugin } from '@capacitor/core';
export class FilePickerWeb extends WebPlugin {
    constructor() {
        super(...arguments);
        this.ERROR_PICK_FILE_CANCELED = 'pickFiles canceled.';
        this.TOO_MANY_FILES = 'TOO_MANY_FILES.';
    }
    async convertHeicToJpeg(_options) {
        throw this.unimplemented('Not implemented on web.');
    }
    async pickFiles(options) {
        const pickedFiles = await this.openFilePicker(options);
        if (!pickedFiles) {
            throw new Error(this.ERROR_PICK_FILE_CANCELED);
        }
        const result = {
            files: [],
        };
        for (const pickedFile of pickedFiles) {
            const file = {
                blob: pickedFile,
                modifiedAt: pickedFile.lastModified,
                mimeType: this.getMimeTypeFromUrl(pickedFile),
                name: this.getNameFromUrl(pickedFile),
                path: undefined,
                size: this.getSizeFromUrl(pickedFile),
            };
            if (options === null || options === void 0 ? void 0 : options.readData) {
                file.data = await this.getDataFromFile(pickedFile);
            }
            result.files.push(file);
        }
        return result;
    }
    async pickImages(options) {
        return this.pickFiles(Object.assign({ types: ['image/*'] }, options));
    }
    async pickMedia(options) {
        return this.pickFiles(Object.assign({ types: ['image/*', 'video/*'] }, options));
    }
    async pickVideos(options) {
        return this.pickFiles(Object.assign({ types: ['video/*'] }, options));
    }
    async openFilePicker(options) {
        var _a;
        const accept = ((_a = options === null || options === void 0 ? void 0 : options.types) === null || _a === void 0 ? void 0 : _a.join(',')) || '';
        const multiple = !!(options === null || options === void 0 ? void 0 : options.multiple);
        const maximumFilesCount = (options === null || options === void 0 ? void 0 : options.maximumFilesCount) || 15;
        return new Promise((resolve, reject) => {
            let onChangeFired = false;
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = accept;
            input.multiple = multiple;
            input.addEventListener('change', (event) => {
                var _a, _b;
                if (((_b = (_a = event.target) === null || _a === void 0 ? void 0 : _a.files) === null || _b === void 0 ? void 0 : _b.length) > maximumFilesCount) {
                    reject(this.TOO_MANY_FILES);
                }
                onChangeFired = true;
                const files = Array.from(input.files || []);
                resolve(files);
            }, { once: true });
            // Workaround to detect when Cancel is selected in the File Selection dialog box.
            window.addEventListener('focus', async () => {
                await this.wait(1000);
                if (onChangeFired) {
                    return;
                }
                resolve(undefined);
            }, { once: true });
            input.click();
        });
    }
    async getDataFromFile(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => {
                const result = typeof reader.result === 'string' ? reader.result : '';
                const splittedResult = result.split('base64,');
                const base64 = splittedResult[1] || '';
                resolve(base64);
            };
            reader.onerror = error => {
                reject(error);
            };
        });
    }
    getNameFromUrl(file) {
        return file.name;
    }
    getMimeTypeFromUrl(file) {
        return file.type;
    }
    getSizeFromUrl(file) {
        return file.size;
    }
    async wait(delayMs) {
        return new Promise(resolve => setTimeout(resolve, delayMs));
    }
}
//# sourceMappingURL=web.js.map