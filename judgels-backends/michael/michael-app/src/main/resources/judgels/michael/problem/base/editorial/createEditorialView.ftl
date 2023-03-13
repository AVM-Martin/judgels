<#-- @ftlvariable type="judgels.michael.problem.base.editorial.CreateEditorialView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<@template.layout>
  <h3>New editorial</h3>
  <@forms.form>
    <@forms.select form=form name="initialLanguage" label="Initial language" options=languages/>
    <@forms.submit>Create</@forms.submit>
  </@forms.form>
</@template.layout>
