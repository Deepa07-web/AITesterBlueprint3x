import fs from 'fs';
import * as pdfjsLib from 'pdfjs-dist/legacy/build/pdf.mjs';

const data = new Uint8Array(fs.readFileSync('Restful-booker.pdf'));
const loadingTask = pdfjsLib.getDocument({ data });
const doc = await loadingTask.promise;
console.log('pages:', doc.numPages);
let text = '';
for (let i = 1; i <= doc.numPages; i += 1) {
  const page = await doc.getPage(i);
  const content = await page.getTextContent();
  const pageText = content.items.map(item => item.str).join(' ');
  text += pageText + '\n\n';
}
console.log(text.slice(0, 12000));
