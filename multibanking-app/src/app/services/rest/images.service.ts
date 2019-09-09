import { Injectable } from '@angular/core';
import { AbstractService } from './abstract.service';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ImagesService extends AbstractService {

  getImage(imageName: string) {
    if (!imageName) {
      return `${environment.smartanalytics_url}/images/keinlogo_256.png`;
    }
    return `${environment.smartanalytics_url}/images/${imageName}`;
  }

  uploadImages(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('imagesFile', file, file.name);

    return this.http.post(`${environment.smartanalytics_url}/images/upload`, formData, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }
}
