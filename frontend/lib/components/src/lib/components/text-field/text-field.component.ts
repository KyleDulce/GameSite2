import { ChangeDetectorRef, Component, computed, effect, inject, input, Optional, output, Self, signal } from '@angular/core';
import { ControlValueAccessor, NgControl, Validators } from '@angular/forms';

type TextFieldTypes = 'text' | 'textarea' | 'password';

@Component({
  selector: 'gs-text-field',
  templateUrl: './text-field.component.html',
  styleUrl: './text-field.component.scss',
})
export class TextFieldComponent implements ControlValueAccessor {
  private parentControl = inject(NgControl, {
    optional: true, 
    self: true
  });

  public type = input<TextFieldTypes>('text');
  public value = input<string>('');
  public placeholder = input<string>('');
  public disabled = input<string>('');
  public required = input<boolean>(false);

  public onValueChanged = output<string>();

  protected fieldContent: string = '';
  protected touched = signal<boolean>(false);
  protected controlDisabled = computed(() => this.disabledByCode() || !!this.disabled());
  protected focus = signal<boolean>(false);

  private disabledByCode = signal<boolean>(false);
  private onTouched: Function = () => {};
  private onChangedForm: Function = (value: string) => {};

  constructor() {
    if(this.parentControl) {
      this.parentControl.valueAccessor = this;
    }
    effect(() => {
      this.fieldContent = this.value();
    });
  }

  protected onFieldContentChange(newText: string): void {
    this.fieldContent = newText;
    this.onValueChanged.emit(newText);
    this.onChangedForm(newText);
  }

  protected onFocus(): void {
    this.focus.set(true);
  }

  protected onBlur(): void {
    this.focus.set(false);
    if(!this.touched()) {
      this.touched.set(true);
      this.onTouched();
    }
  }

  writeValue(obj: any): void {
    this.fieldContent = obj;
  }

  registerOnChange(fn: any): void {
    this.onChangedForm = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabledByCode.set(isDisabled);
  }

  protected shouldShowErrored(): boolean {
    const control = this.parentControl?.control;
    if(!this.touched()) {
      return false;
    }

    if(this.required() && !this.fieldContent) {
      return true;
    }

    if(!control) {
      return false;
    }

    return !!control.errors;
  }
}
