import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-loading-dots',
  standalone: true,
  imports: [],
  templateUrl: './loading-dots.component.html',
  styleUrl: './loading-dots.component.scss'
})
export class LoadingDotsComponent {
  @Input() isLoading = false;
  dots = '';
  private intervalId: any;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isLoading']) {
      if (this.isLoading) {
        this.startLoading();
      } else {
        this.stopLoading();
      }
    }
  }


  startLoading(){
    this.isLoading = true;
    let count:number = 0;
    this.intervalId = setInterval(() => {
      count = (count + 1) % 4;
      this.dots = ".".repeat(count);
    }, 400);
  }

  private stopLoading(): void {
    if (this.intervalId !== null) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
    this.dots = '';
  }

  ngOnDestroy(){
    clearInterval(this.intervalId);
  }

  testButtonText:string = 'Load';
  toggleLoading(){
    this.isLoading = !this.isLoading;
    if(this.isLoading === true){
      this.testButtonText = 'Stop loading';
      this.startLoading();
    }else{
      this.testButtonText = 'Load';
      this.stopLoading();
    }
  }

}
