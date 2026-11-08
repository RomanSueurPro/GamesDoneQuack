import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { UserProfile } from '../../../models/UserProfile';
import { AuthStateService } from '../../../services/auth-state.service';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.scss'
})
export class EditComponent {

  fileName = '';
  userProfile: UserProfile | null = null;
  id = 0;

  constructor(private http: HttpClient, private authStateService: AuthStateService) { }

  form = new FormGroup({
    id: new FormControl(-1),
    username: new FormControl(""),
    email: new FormControl(""),
    bio: new FormControl(""),
    avatar: new FormControl<File | null>(null),

  });

  ngOnInit(): void {

    this.userProfile = null;


    this.fetchUserInfoObservableById(this.authStateService.user()?.id)
      .subscribe({
        next: profile => {
          this.userProfile = profile;
          this.updateFullForm(profile);
        },
        error: error => {
          console.error(error);
          this.userProfile = null;
        }
      });
  }

  updateFullForm(profile: UserProfile) {
    this.form.patchValue({
      id: profile.id,
      username: profile.username,
      bio: profile.bio,
      email: profile.email,
    });
  }

  fetchUserInfoObservableById(id: number | undefined): Observable<any> {
    return this.http.post<UserProfile>(
      API_ENDPOINTS.user.userInfoById, id, { withCredentials: true });
  }



  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    const file: File | undefined = input.files?.[0];

    if (file) {
      this.fileName = file.name;

      const formData = new FormData();
      formData.append('file', file);
      formData.append('profile', new Blob(
        [JSON.stringify(this.userProfile)],
        { type: 'application/json' }
      ));

      this.http.patch(API_ENDPOINTS.user.avatarUpload, formData, { withCredentials: true })
        .subscribe({
          next: response => console.log(response),
          error: error => console.error(error)
        });
    }
  }

  saveChanges() {
    this.saveChangesObservable().subscribe({
      next: () => console.log('test complete'),
    });
  }

  saveChangesObservable(): Observable<any> {
    console.log(this.form);
    const formData = new FormData();

    formData.append('profile', new Blob(
      [JSON.stringify({
        id: this.form.controls.id.value,
        username: this.form.controls.username.value,
        email: this.form.controls.email.value,
        bio: this.form.controls.bio.value,
        avatarFileName: this.userProfile?.avatarFileName,
      })],
      { type: 'application/json' }
    ));

    const avatar = this.form.controls.avatar.value;

    if(avatar){
      formData.append('file', avatar);
    }

    return this.http.patch(API_ENDPOINTS.user.editProfile, formData, { withCredentials: true });
  }

  onAvatarSelected(event: Event) {
    console.log(event);
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    this.form.controls.avatar.setValue(file);
    this.form.controls.avatar.markAsDirty();
  }

}
